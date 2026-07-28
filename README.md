# TradeGuard – AI-Assisted Trading Controls Microservice

[![Build](https://github.com/tu-usuario/tradeguard/actions/workflows/build.yml/badge.svg)](https://github.com/tu-usuario/tradeguard/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tradeguard&metric=alert_status)](https://sonarcloud.io/dashboard?id=tradeguard)

**TradeGuard** es un microservicio Spring Boot que valida órdenes bursátiles aplicando reglas de riesgo y compliance. Este proyecto demuestra un flujo completo de **AI-Assisted Code Analysis & Refactoring**, elevando código legacy vulnerable a un estándar enterprise seguro y mantenible, utilizando herramientas gratuitas de IA como **Codeium**.

## 🎯 Objetivo
Mostrar cómo un AI-Assisted Code Analyst (rol Talan) puede transformar una base de código con deuda técnica y vulnerabilidades en una solución robusta lista para CI/CD, empleando asistentes de IA en cada etapa.

## 🧠 Herramienta de IA utilizada
[Codeium](https://codeium.com/) – asistente gratuito para VS Code (chat, autocompletado, comandos `/explain`, `/refactor`). No requiere token ni suscripción.

## 🧟 Estado inicial (rama `legacy`)
El código original contenía intencionadamente:
- Lógica de negocio en controlador
- Falta de validación (sin Bean Validation, sin DTOs)
- Vulnerabilidad SQL Injection (JPQL concatenado)
- Secreto hardcodeado en `application.properties`
- Cero tests unitarios
- Sin manejo centralizado de errores
- Ausencia de seguridad (sin API Key)

### Análisis de Codeium
Seleccionando el controlador y pidiendo `> Explain this code and list all code smells, security vulnerabilities...` se obtuvo un reporte detallado (ver `docs/initial-analysis.md`). Principales hallazgos: 3 bugs, 5 code smells, 2 vulnerabilidades críticas (inyección SQL, exposición de secretos).

## 🔄 Proceso de refactorización asistida por IA
Cada mejora se realizó mediante un prompt en Codeium y se reflejó en un commit atómico:

| Commit | Prompt utilizado | Resultado |
|--------|------------------|-----------|
| `refactor: extract validation service` | `> Refactor controller method extracting all business logic into a new TradeValidationService...` | Servicio separado con constructor injection |
| `feat: add DTOs and Bean Validation` | `> Create TradeRequest DTO with @NotBlank, @Positive...` | DTOs, controlador usa @Valid |
| `fix: use parameterized JPQL query` | `> Replace JPQL concatenation with @Query annotation...` | Repositorio con método seguro `findByTraderName` |
| `security: externalize secret and add API key filter` | `> Remove hardcoded secret, load from env variable and implement a filter checking X-API-Key` | `application.properties` usa variables de entorno, filtro de seguridad |
| `quality: centralize reject reasons and add logging` | `> Use constants for reject reasons and add SLF4J logging in service` | Código limpio, trazabilidad |
| `test: generate JUnit tests for service and controller` | `> Generate JUnit 5 tests with Mockito for TradeValidationService...` y `> Generate MockMvc tests...` | Cobertura >80% |

## 📈 Métricas de calidad (antes/después)
| Indicador | Legacy | Refactorizado |
|-----------|--------|---------------|
| Code Smells | 12 | 0 |
| Vulnerabilidades | 2 (inyección) | 0 |
| Bugs | 3 | 0 |
| Cobertura de tests | 0% | 84% |
| Duplicación | 5% | 0% |
| Deuda técnica | 2h 30min | 0min |

*(basado en análisis de SonarCloud)*

## 🔒 Seguridad
- Validación de entrada con Bean Validation.
- Consultas parametrizadas para prevenir inyección SQL.
- API Key obligatoria en cabecera `X-API-Key` (configurable por variable de entorno).
- Secretos externalizados (`APP_SECRET`, `API_KEY`), nunca en el código.

## 🚀 CI/CD
Cada push a `main` dispara el pipeline de GitHub Actions:
1. Checkout y configuración de JDK 17.
2. Compilación y ejecución de tests (`mvn clean verify`).
3. Análisis de SonarCloud con reporte de calidad.
El badge en este README refleja el estado actual.

## 🛠️ Stack tecnológico
- Java 17, Spring Boot 3.2
- Spring Web, Data JPA, Validation
- H2 Database (modo Oracle)
- Maven, JaCoCo, SonarCloud
- JUnit 5, Mockito
- Codeium (asistente IA)

## 🏃 Cómo ejecutar localmente
```bash
git clone https://github.com/tu-usuario/tradeguard.git
cd tradeguard
mvn spring-boot:run