Invoke-RestMethod -Uri "http://localhost:8081/api/meteo/stations" -Method GET | ConvertTo-Json -Depth 10
