# Port Availability Checker for Microservices
# Checks if all required ports are free before starting services

# Port definitions
$ports = @(
    @{Port=8761; Service="Service Registry (Eureka)"; Required=$true},
    @{Port=7100; Service="Config Server"; Required=$true},
    @{Port=8099; Service="API Gateway"; Required=$true},
    @{Port=8300; Service="Cart Service"; Required=$true},
    @{Port=8100; Service="Order Service"; Required=$true},
    @{Port=8900; Service="Inventory Service"; Required=$true},
    @{Port=16686; Service="Jaeger UI"; Required=$false},
    @{Port=4318; Service="Jaeger OTLP Collector"; Required=$false}
)

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   Port Availability Checker" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$portsInUse = @()
$portsAvailable = @()

foreach ($portInfo in $ports) {
    $port = $portInfo.Port
    $service = $portInfo.Service
    $required = $portInfo.Required
    
    # Check if port is in use
    $connection = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    
    if ($connection) {
        $processId = $connection.OwningProcess
        $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
        
        Write-Host "[X] Port $port - " -NoNewline -ForegroundColor Red
        Write-Host "IN USE     " -NoNewline -ForegroundColor Red
        Write-Host "- $service" -ForegroundColor White
        
        if ($process) {
            Write-Host "    Process: $($process.ProcessName) (PID: $processId)" -ForegroundColor Yellow
            Write-Host "    Path: $($process.Path)" -ForegroundColor Gray
        } else {
            Write-Host "    Process ID: $processId" -ForegroundColor Yellow
        }
        
        Write-Host ""
        $portsInUse += @{Port=$port; Service=$service; ProcessId=$processId; ProcessName=$process.ProcessName; Required=$required}
    } else {
        Write-Host "[OK] Port $port - " -NoNewline -ForegroundColor Green
        Write-Host "AVAILABLE " -NoNewline -ForegroundColor Green
        Write-Host "- $service" -ForegroundColor White
        
        $portsAvailable += @{Port=$port; Service=$service}
    }
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   Summary" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Ports Available: " -NoNewline
Write-Host "$($portsAvailable.Count) / $($ports.Count)" -ForegroundColor Green

Write-Host "Ports In Use: " -NoNewline
if ($portsInUse.Count -eq 0) {
    Write-Host "$($portsInUse.Count)" -ForegroundColor Green
} else {
    Write-Host "$($portsInUse.Count)" -ForegroundColor Red
}

Write-Host ""

if ($portsInUse.Count -eq 0) {
    Write-Host "[SUCCESS] All ports are available!" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can start the services now:" -ForegroundColor White
    Write-Host "  - Docker:  docker-compose up -d" -ForegroundColor Cyan
    Write-Host "  - Manual:  See README.md for startup sequence" -ForegroundColor Cyan
} else {
    # Check if any required ports are in use
    $requiredPortsInUse = $portsInUse | Where-Object { $_.Required -eq $true }
    
    if ($requiredPortsInUse.Count -gt 0) {
        Write-Host "[ERROR] Required ports are in use!" -ForegroundColor Red
        Write-Host ""
        Write-Host "Required ports in use:" -ForegroundColor Yellow
        foreach ($portInfo in $requiredPortsInUse) {
            Write-Host "  - Port $($portInfo.Port): $($portInfo.Service)" -ForegroundColor Yellow
        }
    } else {
        Write-Host "[WARNING] Optional ports are in use (services will still work)" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "Options to free up ports:" -ForegroundColor White
    Write-Host ""
    
    Write-Host "1. Stop specific processes:" -ForegroundColor Cyan
    foreach ($portInfo in $portsInUse) {
        if ($portInfo.ProcessName) {
            Write-Host "   Stop-Process -Name '$($portInfo.ProcessName)' -Force" -ForegroundColor Gray
        } else {
            Write-Host "   Stop-Process -Id $($portInfo.ProcessId) -Force" -ForegroundColor Gray
        }
    }
    
    Write-Host ""
    Write-Host "2. Stop all Java processes:" -ForegroundColor Cyan
    Write-Host "   Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force" -ForegroundColor Gray
    
    Write-Host ""
    Write-Host "3. Stop Docker containers:" -ForegroundColor Cyan
    Write-Host "   docker-compose down" -ForegroundColor Gray
    
    Write-Host ""
    Write-Host "4. Change ports in configuration files:" -ForegroundColor Cyan
    Write-Host "   Edit application.properties in each service" -ForegroundColor Gray
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Create detailed report
$reportPath = "port-check-report.txt"
$report = @"
Port Availability Report
Generated: $(Get-Date)
================================================

Ports Available: $($portsAvailable.Count) / $($ports.Count)
Ports In Use: $($portsInUse.Count)

Detailed Status:
================================================

Available Ports:
"@

foreach ($portInfo in $portsAvailable) {
    $report += "`n  [OK] Port $($portInfo.Port) - $($portInfo.Service)"
}

if ($portsInUse.Count -gt 0) {
    $report += "`n`nPorts In Use:"
    foreach ($portInfo in $portsInUse) {
        $report += "`n  [X] Port $($portInfo.Port) - $($portInfo.Service)"
        if ($portInfo.ProcessName) {
            $report += "`n      Process: $($portInfo.ProcessName) (PID: $($portInfo.ProcessId))"
        } else {
            $report += "`n      Process ID: $($portInfo.ProcessId)"
        }
    }
    
    $report += "`n`nRecommended Actions:"
    $report += "`n  1. Stop conflicting processes"
    $report += "`n  2. Use 'docker-compose down' to stop containers"
    $report += "`n  3. Change ports in service configuration files"
}

$report += "`n`n================================================"
$report += "`nEnd of Report"

# Save report
$report | Out-File -FilePath $reportPath -Encoding UTF8

Write-Host "Detailed report saved to: $reportPath" -ForegroundColor Cyan
Write-Host ""
