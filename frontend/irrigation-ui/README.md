# Irrigation System - Frontend Angular

## Installation

```bash
cd frontend/irrigation-ui
npm install
```

## Lancer l'application

```bash
ng serve
```

L'application sera disponible sur `http://localhost:4200`

## Structure

- **components/**: Composants Angular
  - meteo: Gestion des stations météo et prévisions
  - arrosage: Gestion des programmes et journaux d'arrosage
  - dashboard: Tableau de bord principal

- **services/**: Services pour communiquer avec le backend
  - meteo.service.ts
  - arrosage.service.ts

## Build pour production

```bash
ng build --prod
```
