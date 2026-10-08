package com.nemiliano.qa.demo.data;

import com.nemiliano.qa.data.TestCase;

/**
 * Una fila de {@code data/login.csv}. {@code casoDePrueba} es solo metadata; {@code
 * resultadoEsperado} es el texto que debe mostrar la página (inventario o mensaje de error).
 */
public record LoginData(
    String casoDePrueba, String usuario, String clave, boolean exito, String resultadoEsperado)
    implements TestCase {}
