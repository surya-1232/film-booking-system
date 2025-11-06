package com.bus.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sales_report")
public class SalesReport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "movie_Id")
	private Long movieId;
	
	@Column(name = "movie_name")
	private String movieName;
	
	@Column(name = "start_date")
	private String startDate;
	
	@Column(name = "end_date")
	private String endDate;
	
	@Column(name = "ticket_solds")
	private int ticketSolds;
	
	@Column(name = "ticket_price")
	private double avgTicketPrice;
	
	@Column(name = "total_sales")
	private double totalSales;
	
	@Column(name = "status")
	private String status;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMovieId() {
		return movieId;
	}

	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getTicketSolds() {
		return ticketSolds;
	}

	public void setTicketSolds(int ticketSolds) {
		this.ticketSolds = ticketSolds;
	}

	public double getAvgTicketPrice() {
		return avgTicketPrice;
	}

	public void setAvgTicketPrice(double avgTicketPrice) {
		this.avgTicketPrice = avgTicketPrice;
	}

	public double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}

	public String getStatus() {
		return status;
	}

	public SalesReport() {
		super();
	}

	public SalesReport(Long id, Long movieId, String movieName, String startDate, String endDate, int ticketSolds,
			double avgTicketPrice, double totalSales, String status) {
		super();
		this.id = id;
		this.movieId = movieId;
		this.movieName = movieName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.ticketSolds = ticketSolds;
		this.avgTicketPrice = avgTicketPrice;
		this.totalSales = totalSales;
		this.status = status;
	}
	
	public SalesReport(String movieName, String startDate, String endDate, int ticketSolds,
			double avgTicketPrice, double totalSales, String status) {
		super();
		this.movieName = movieName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.ticketSolds = ticketSolds;
		this.avgTicketPrice = avgTicketPrice;
		this.totalSales = totalSales;
		this.status = status;
	}

	@Override
	public String toString() {
		return "SalesReport [id=" + id + ", movieId=" + movieId + ", movieName=" + movieName + ", startDate="
				+ startDate + ", endDate=" + endDate + ", ticketSolds=" + ticketSolds + ", avgTicketPrice="
				+ avgTicketPrice + ", totalSales=" + totalSales + ", status=" + status + "]";
	}

	public void setStatus(String status) {
		this.status = status;
	}


}
