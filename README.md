# qa-tests-demo

Proyecto de práctica que **consume** [`qa-framework-core`](../qa-framework-core). Sirve para aprender y como modelo de cómo organizar un proyecto de tests sin tocar el framework.

## Requisitos
- JDK 25 LTS, Git. Maven no hace falta (se usa `mvnw`).
- `qa-framework-core` instalado en tu `~/.m2` (en la carpeta del core: `.\mvnw.cmd clean install`).
- Chrome instalado (Selenium) y Chromium de Playwright (ver el README del core).
- Internet: dos sitios públicos de práctica ([saucedemo](https://www.saucedemo.com) y [restful-booker](https://restful-booker.herokuapp.com)).

## Qué hay

| Tipo | Test | Sitio | Tags |
|---|---|---|---|
| UI Selenium | `ShoppingSeleniumTest` | saucedemo | `ui` `selenium` `smoke` |
| UI Playwright (**mismo escenario**) | `ShoppingPlaywrightTest` | saucedemo | `ui` `playwright` `smoke` |
| Data-driven (CSV + "Caso de prueba") | `LoginDataDrivenTest` | saucedemo | `ui` `playwright` `regression` |
| API | `RestfulBookerApiTest` | restful-booker | `api` (`smoke` / `regression`) |
| API + UI | `ReservaApiUiTest` | app local | `api` `ui` `playwright` `regression` |
| Validación en DB | `ReservaDbTest` | app local (H2) | `api` `db` `regression` |
| **Falla a propósito** | `DemoFailureTest` | app local | `demo-failure` (excluido por defecto) |

La **app local** (`DemoApp`) es un mini sistema bajo prueba (API REST + página HTML + base H2) que se levanta sola dentro de los tests: permite practicar API + UI + DB sin depender de Internet.

## Cómo ejecutar
```powershell
.\mvnw.cmd clean test                       # todo, menos los que fallan a propósito
.\mvnw.cmd clean test -Dgroups=smoke        # solo smoke
.\mvnw.cmd clean test -Dgroups="api & ui"   # expresión de tags
.\mvnw.cmd clean test -Dtest=SmokeSuite     # una suite (SmokeSuite, RegressionSuite)
.\mvnw.cmd clean test -DexcludedGroups=none -Dgroups=demo-failure   # los que fallan a propósito
.\mvnw.cmd clean verify                     # además verifica el formato (Spotless)
```
Con propiedades o variables de ambiente se cambia el comportamiento (ver el README del core):
```powershell
.\mvnw.cmd test -Dheadless=false -Dslowmo.ms=500      # ver el navegador, en cámara lenta
$env:HEADLESS="false"; .\mvnw.cmd test                # igual, con variable de ambiente
```

## Reporte Allure
Cada ejecución deja `target/allure-results` (con categorías de fallos e info de ambiente). Para verlo:
```powershell
.\mvnw.cmd allure:serve      # genera y abre el reporte en el navegador
```
La primera vez descarga el generador de reportes (allure-commandline) desde Maven Central.
Cada fila de datos aparece con su **Caso de prueba**, los datos de entrada y el resultado esperado; los tests fallidos traen screenshot, HTML y (Playwright) el `trace.zip`.

Evidencias también quedan en `target/evidence/`. Para abrir un trace de Playwright:
```powershell
cd ..\qa-framework-core
.\mvnw.cmd -q -pl ui-playwright exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=show-trace ..\qa-tests-demo\target\evidence\<archivo>_trace.zip"
```

## Cómo agregar un test nuevo
1. **UI:** creá el Page Object en `pages/selenium` o `pages/playwright` (un locator por elemento, métodos con nombre de negocio).
2. Creá la clase `XxxTest` en `tests/<tipo>/` con `@ExtendWith({QaExtension.class, PlaywrightExtension.class})` (o `SeleniumExtension`) y recibí `Page` / `WebDriver` / `QaConfig` como parámetros del método.
3. Poné los **tags** (`@Tag("smoke")`, `@Tag("ui")`...) y un `@DisplayName` en español.
4. Estructura AAA (Arrange-Act-Assert) y una aserción lógica por test.
5. Datos: si hay varias combinaciones, un CSV en `src/test/resources/data/` con la columna **Caso de prueba**, un `record` que implemente `TestCase` y `@CsvTestData`.
6. Datos que el test crea: borralos al final (`@AfterEach`). Usá nombres únicos (`BookingBuilder.uniqueGuest()`) para poder correr en paralelo.

Estructura:
```
src/test/java/com/nemiliano/qa/demo/
  app/        app local de práctica (sistema bajo prueba)
  data/       records de datos y builders (Datafaker)
  pages/      Page Objects (selenium/, playwright/)
  tests/      ui/, api/, integration/, failure/
  suites/     SmokeSuite, RegressionSuite
src/test/resources/  config/local.properties, data/*.csv, junit-platform.properties
src/test/allure/     environment.properties, categories.json (se copian a allure-results)
```

## Notas
- Las credenciales de saucedemo y restful-booker son **públicas** (publicadas por esos sitios de práctica). En un proyecto real, usuarios y claves van en variables de ambiente o secretos, nunca en el repo.
- Paralelismo: 2 clases a la vez (`junit-platform.properties`). Con poca memoria, bajalo a 1.
