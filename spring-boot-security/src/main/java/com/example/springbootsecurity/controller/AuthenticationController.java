package com.example.springbootsecurity.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootsecurity.config.CustomUserDetailService;
import com.example.springbootsecurity.config.JwtUtil;
import com.example.springbootsecurity.model.AuthenticationRequest;
import com.example.springbootsecurity.model.AuthenticationResponse;
import com.example.springbootsecurity.model.UserDTO;


import io.jsonwebtoken.impl.DefaultClaims;

@RestController
public class AuthenticationController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private CustomUserDetailService userDetailService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	/**
	 * @param authenticationRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/authenticate", method=RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception
	{
		try
		{
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		}
		catch(DisabledException e)
		{
			throw new Exception("USER_DISABLED",e);
		}
		catch(BadCredentialsException e)
		{
			throw new Exception("INVALID_CREDENTIALS",e);
		}
		
		UserDetails userDetails=userDetailService.loadUserByUsername(authenticationRequest.getUsername());
		String token=jwtUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(token));
		
	
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception
	{
		return ResponseEntity.ok(userDetailService.save(user));
	}

	@RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
	public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
		// From the HttpRequest get the claims
		DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");

		Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
		String token = jwtUtil.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());
		return ResponseEntity.ok(new AuthenticationResponse(token));
	}

	public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		for (Entry<String, Object> entry : claims.entrySet()) {
			expectedMap.put(entry.getKey(), entry.getValue());
		}
		return expectedMap;
	}
}
