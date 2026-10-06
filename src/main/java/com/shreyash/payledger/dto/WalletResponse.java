package com.shreyash.payledger.dto;

import java.math.BigDecimal;

public record WalletResponse(Long id, BigDecimal balance, String currency) {}