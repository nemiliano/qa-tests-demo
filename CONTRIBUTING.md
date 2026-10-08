# Contribuir

## Ramas
`main` siempre verde y protegida (cambios por Pull Request). Ramas de trabajo: `feature/<tema>`, `fix/<tema>`, `docs/<tema>`.

## Commits (Conventional Commits)
`<tipo>(<alcance>): <descripción en imperativo>`. Tipos: `feat`, `fix`, `docs`, `test`, `refactor`, `build`, `ci`, `chore`.
Ejemplo: `test(login): agregar caso de usuario bloqueado`.

## Nombres
- Código en inglés; `@DisplayName`, "Caso de prueba" y documentación en español.
- Tests: `XxxTest`. Page Objects: `XxxPage`. Datos: `XxxData` (record que implementa `TestCase`).
- Cada test es independiente: no depende de otro, ni del orden, ni de datos que dejó otro.

## Antes de abrir un PR
1. `.\mvnw.cmd clean verify` en verde.
2. Tests nuevos con tag (`smoke` o `regression`) y tipo (`ui`, `api`, `db`).
3. Sin `Thread.sleep`, sin credenciales reales, sin datos que no limpiás.
