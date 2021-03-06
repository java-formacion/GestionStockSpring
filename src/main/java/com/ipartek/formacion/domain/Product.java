package com.ipartek.formacion.domain;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	private long id;

	@NotNull
	@Size(min = 3, max = 255)
	private String description;

	@Min(0)
	private Double price;

	public Product() {
		super();
		this.id = 0;
		this.description = "";
		this.price = new Double(0);
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public boolean isNew() {
		return (this.id == 0) ? true : false;
	}

	@Override
	public String toString() {
		return "Product [id=" + this.id + ", description=" + this.description + ", price=" + this.price + "]";
	}

}
