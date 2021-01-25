package com.is4103.backend.config;

public class SecurityConstants {
  public static final String SECRET = "xZMhGHjWwCaLHaIMPqNVsNkuNiSvnuIK";
  public static final long EXPIRATION_TIME = 3_600_000; // 1 hr
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";
  public static final String AUTHORITIES_KEY = "role";
}
