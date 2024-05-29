package com.billing.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.OneToMany;

public class Parties {
	private int id;
	private String name;
	private String mobile;
	private String partyGroup;
	private String billingAddress;
	private String shippingAddress;
	private String email;
	private String gstType;
	private String gstinNumber;
	private String state;
	private String openingBalance;
	private String date;
	private String payment;
	private String adharNumber;
	private String panNumber;
	private String drivingLicenceNumber;
	
	@OneToMany
	private List<String> transactions= new ArrayList<>(); //
	
	
}
