param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Username = "traffic-bot",
    [string]$Password = "traffic123",
    [int]$Iterations = 30
)

# Los recursos por-usuario (videojuegos, wishlist, resenas) requieren sesion.
# Se inicia sesion con un usuario dedicado y, si no existe todavia, se registra.
function Get-AuthToken {
    $body = @{ username = $Username; password = $Password } | ConvertTo-Json
    try {
        $resp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/login" -ContentType "application/json" -Body $body
        return $resp.token
    } catch {
        $resp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/register" -ContentType "application/json" -Body $body
        return $resp.token
    }
}

$token = Get-AuthToken
$jsonHeaders = @{
    "Content-Type"  = "application/json"
    "Authorization" = "Bearer $token"
}
$authHeaders = @{
    "Authorization" = "Bearer $token"
}

for ($i = 1; $i -le $Iterations; $i++) {
    Write-Host "Traffic iteration $i/$Iterations"

    Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/videojuegos" -Headers $authHeaders | Out-Null
    Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/videojuegos/estadisticas" -Headers $authHeaders | Out-Null
    Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/wishlist" -Headers $authHeaders | Out-Null

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
