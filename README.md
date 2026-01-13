# 🌱 Système de Gestion de l'Irrigation Automatisée avec Prévisions Météo

Application web en architecture microservices pour la gestion intelligente de l'irrigation basée sur les prévisions météorologiques.

## 📋 Description du Projet

Ce système permet de planifier automatiquement l'irrigation en fonction des conditions météorologiques. Il ajuste dynamiquement la quantité d'eau et le timing d'arrosage selon les prévisions de température, pluie et vent.

### Fonctionnalités Principales

- ✅ **Gestion des stations météo** : Création et suivi de stations météorologiques
- ✅ **Prévisions météorologiques** : Enregistrement et consultation des prévisions (température, pluie, vent)
- ✅ **Planification intelligente** : Création automatique de programmes d'arrosage basés sur la météo
- ✅ **Ajustement dynamique** : Modification automatique des programmes selon les changements météo
- ✅ **Journal d'exécution** : Suivi détaillé des arrosages réalisés
- ✅ **Communication synchrone** : Le service Arrosage interroge le service Météo via API REST
- ✅ **Communication asynchrone** : Le service Météo publie des événements RabbitMQ consommés par Arrosage

## 🏗️ Architecture Microservices

### Microservices Métiers

1. **Meteo-Service** (Port 8081)
   - Gestion des stations météo
   - Gestion des prévisions météorologiques
   - Publication d'événements "ChangementConditions"

2. **Arrosage-Service** (Port 8082)
   - Gestion des programmes d'arrosage
   - Calcul intelligent de la durée et du volume d'eau
   - Consommation des événements météo
   - Journal d'exécution des arrosages

### Microservices Architecturaux

3. **Eureka-Server** (Port 8761)
   - Service Discovery
   - Enregistrement et découverte des microservices

4. **API-Gateway** (Port 8080)
   - Point d'entrée unique
   - Routage des requêtes
   - Configuration CORS

### Infrastructure

- **RabbitMQ** (Ports 5672, 15672) : Message broker pour communication asynchrone
- **MySQL** : Deux bases de données (meteo_db, arrosage_db)
- **Angular** : Interface utilisateur (Port 4200)

## 🛠️ Technologies Utilisées

### Backend
- **Spring Boot 4.0.1** : Framework Java
- **Spring Cloud 2025.1.0** : Netflix Eureka, Spring Cloud Gateway, OpenFeign
- **Spring Cloud Stream** : Communication asynchrone
- **RabbitMQ** : Message broker
- **JPA/Hibernate** : ORM
- **MySQL 8.0** : Base de données
- **Lombok** : Réduction du code boilerplate
- **Maven** : Gestion des dépendances

### Frontend
- **Angular** : Framework TypeScript
- **HttpClient** : Communication avec le backend

### DevOps
- **Docker & Docker Compose** : Conteneurisation
- **Kubernetes** : Orchestration de conteneurs
- **Git** : Gestion de versions


## 🚀 Installation et Exécution

### Prérequis

- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- Node.js 18+ et npm (pour Angular)
- MySQL 8.0 (si exécution locale sans Docker)

### Option 1 : Exécution avec Docker Compose (Recommandé)

1. **Cloner le repository**
```bash
git clone <url-du-repo>
cd irrigation-system
```

2. **Builder les images Docker**
```bash
cd backend/eureka-server
mvn clean package -DskipTests
cd ../meteo-service
mvn clean package -DskipTests
cd ../arrosage-service
mvn clean package -DskipTests
cd ../api-gateway
mvn clean package -DskipTests
```

3. **Lancer tous les services**
```bash
cd ../../docker
docker-compose up -d
```

4. **Vérifier que les services sont démarrés**
```bash
docker-compose ps
```

### Option 2 : Exécution Locale

1. **Lancer RabbitMQ**
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

2. **Créer les bases de données MySQL**
```sql
CREATE DATABASE meteo_db;
CREATE DATABASE arrosage_db;
```

3. **Lancer les microservices dans l'ordre**
```bash
# 1. Eureka Server
cd backend/eureka-server
mvn spring-boot:run

# 2. Meteo Service (nouveau terminal)
cd backend/meteo-service
mvn spring-boot:run

# 3. Arrosage Service (nouveau terminal)
cd backend/arrosage-service
mvn spring-boot:run

# 4. API Gateway (nouveau terminal)
cd backend/api-gateway
mvn spring-boot:run
```

4. **Lancer le Frontend Angular**
```bash
cd frontend/irrigation-ui
npm install
ng serve
```

### Option 3 : Déploiement Kubernetes

1. **Builder les images**
```bash
# Depuis le dossier backend de chaque service
docker build -t irrigation/eureka-server:latest backend/eureka-server
docker build -t irrigation/meteo-service:latest backend/meteo-service
docker build -t irrigation/arrosage-service:latest backend/arrosage-service
docker build -t irrigation/api-gateway:latest backend/api-gateway
```

2. **Déployer sur Kubernetes**
```bash
kubectl apply -f k8s/rabbitmq.yaml
kubectl apply -f k8s/eureka.yaml
kubectl apply -f k8s/meteo.yaml
kubectl apply -f k8s/arrosage.yaml
kubectl apply -f k8s/gateway.yaml
```

3. **Vérifier les déploiements**
```bash
kubectl get pods
kubectl get services
```

## 🌐 Accès aux Services

| Service | URL | Description |
|---------|-----|-------------|
| Angular UI | http://localhost:4200 | Interface utilisateur |
| API Gateway | http://localhost:8080 | Point d'entrée API |
| Eureka Dashboard | http://localhost:8761 | Console Eureka |
| RabbitMQ Management | http://localhost:15672 | Console RabbitMQ (guest/guest) |
| Meteo Service | http://localhost:8081/api/meteo | API Météo (direct) |
| Arrosage Service | http://localhost:8082/api/arrosage | API Arrosage (direct) |

## 📡 Endpoints API Principaux

### Service Météo (via Gateway: http://localhost:8080/api/meteo)

**Stations Météo**
- `GET /stations` : Liste des stations
- `POST /stations` : Créer une station
- `PUT /stations/{id}` : Modifier une station
- `DELETE /stations/{id}` : Supprimer une station

**Prévisions**
- `GET /previsions/all` : Toutes les prévisions
- `GET /previsions?stationId={id}&date={date}` : Prévisions pour une station et date
- `POST /previsions` : Créer une prévision (publie un événement RabbitMQ)
- `PUT /previsions/{id}` : Modifier une prévision
- `DELETE /previsions/{id}` : Supprimer une prévision

### Service Arrosage (via Gateway: http://localhost:8080/api/arrosage)

**Programmes**
- `GET /programmes` : Liste des programmes
- `GET /programmes/parcelle/{id}` : Programmes d'une parcelle
- `POST /programmes/avec-meteo?parcelleId={id}&stationMeteoId={id}&date={date}` : Créer un programme intelligent
- `POST /programmes/{id}/executer` : Exécuter un programme
- `PUT /programmes/{id}` : Modifier un programme
- `DELETE /programmes/{id}` : Supprimer un programme

**Journaux**
- `GET /journaux` : Liste des journaux
- `GET /journaux/programme/{id}` : Journaux d'un programme

## 🔄 Communication Entre Services

### Communication Synchrone (REST/OpenFeign)
```
Arrosage Service → [Feign Client] → Meteo Service
```
Utilisé pour récupérer les prévisions lors de la création d'un programme d'arrosage.

### Communication Asynchrone (RabbitMQ)
```
Meteo Service → [RabbitMQ: meteo-conditions-topic] → Arrosage Service
```
Le service Météo publie un événement `ChangementConditionsEvent` lors de la création/modification d'une prévision. Le service Arrosage consomme cet événement et ajuste automatiquement les programmes d'arrosage planifiés.

## 🧪 Test du Système

### Scénario de Test Complet

1. **Créer une station météo**
```bash
curl -X POST http://localhost:8080/api/meteo/stations \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Station Paris Nord",
    "latitude": 48.8566,
    "longitude": 2.3522,
    "fournisseur": "MeteoFrance"
  }'
```

2. **Créer une prévision (déclenche un événement RabbitMQ)**
```bash
curl -X POST http://localhost:8080/api/meteo/previsions \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2026-01-20",
    "temperatureMax": 28.0,
    "temperatureMin": 18.0,
    "pluiePrevue": false,
    "vent": 15.0,
    "station": {"id": 1}
  }'
```

3. **Créer un programme d'arrosage intelligent**
```bash
curl -X POST "http://localhost:8080/api/arrosage/programmes/avec-meteo?parcelleId=1&stationMeteoId=1&date=2026-01-20"
```

4. **Exécuter le programme**
```bash
curl -X POST http://localhost:8080/api/arrosage/programmes/1/executer
```

5. **Vérifier les journaux**
```bash
curl http://localhost:8080/api/arrosage/journaux
```

## 📊 Modèle de Données

### Service Météo

**Prevision**
- id, date, temperatureMax, temperatureMin, pluiePrevue, vent
- Relation ManyToOne avec StationMeteo

**StationMeteo**
- id, nom, latitude, longitude, fournisseur

### Service Arrosage

**ProgrammeArrosage**
- id, parcelleId, datePlanifiee, duree, volumePrevu, statut
- temperatureMax, temperatureMin, pluiePrevue, vent, stationMeteoId
- Statut: PLANIFIE, EN_COURS, TERMINE, ANNULE, AJUSTE

**JournalArrosage**
- id, programmeId, dateExecution, volumeReel, remarque
- typeExecution: AUTOMATIQUE, MANUEL, URGENCE