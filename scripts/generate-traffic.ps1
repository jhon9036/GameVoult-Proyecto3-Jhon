param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ApiKey = "dev-gamevault-key",
    [int]$Iterations = 30
)

$jsonHeaders = @{
    "Content-Type" = "application/json"
    "X-API-Key" = $ApiKey
}

$authHeaders = @{
    "X-API-Key" = $ApiKey
}

for ($i = 1; $i -le $Iterations; $i++) {
    Write-Host "Traffic iteration $i/$Iterations"

    Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/videojuegos" | Out-Null
    Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/videojuegos/estadisticas" | Out-Null
    Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/wishlist" | Out-Null

    if ($i % 5 -eq 0) {
        $body = @{
            titulo = "Demo traffic $i"
            prioridad = "BAJA"
            notas = "Item temporal generado por script de trafico"
        } | ConvertTo-Json

        $item = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/wishlist" -Headers $jsonHeaders -Body $body
        if ($item.id) {
            Invoke-RestMethod -Method Delete -Uri "$BaseUrl/api/wishlist/$($item.id)" -Headers $authHeaders | Out-Null
        }
    }

    Start-Sleep -Milliseconds 500
}

Write-Host "Traffic generation finished. Open Grafana at http://localhost:3000"
