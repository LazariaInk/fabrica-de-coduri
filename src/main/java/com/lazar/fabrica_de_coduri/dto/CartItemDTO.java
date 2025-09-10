package com.lazar.fabrica_de_coduri.dto;

import java.math.BigDecimal;

public record CartItemDTO(Long courseId, String title, BigDecimal price) {}