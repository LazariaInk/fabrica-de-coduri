package com.lazar.fabrica_de_coduri.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartSummaryDTO(int count, BigDecimal total, List<CartItemDTO> items) {}