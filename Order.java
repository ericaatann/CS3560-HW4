package homework;

import javax.persistence.Entity;
//import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.OneToOne;
//import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
//import javax.persistence.CascadeType;

//import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.ArrayList;
import java.sql.Statement;

import jdbc.ConnectionFactory;

import java.util.Date;

import homework.Customer;
import homework.Address;


@Entity(name = "order")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "number")
	private int number;
	@Column(name = "date")
	private Date date;
	@Column(name = "item")
	private String item;
	@Column(name = "price")
	private double price;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "customer_id")
	private Customer customer;

	public Order(int number, Date date, String item, double price) {
		this.number = number;
		this.date = date;
		this.item = item;
		this.price = price;
	}

	public Order() {
		super();
	}

	public static void main(String[] args) {
		// create a new Order object with some sample data
		Order order = new Order(1, new Date(), "Product A", 10.0);

		// create a new Customer object with some sample data
		Customer customer = new Customer("John", "555-1234", "john.doe@example.com");

		// set the Customer object as the customer for the Order object
		order.setCustomer(customer);

		// create a new Hibernate session factory and get the current session
		SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();

		try {
			// start a new transaction
			session.beginTransaction();

			// save the Customer object to the database
			session.save(customer);

			// save the Order object to the database
			session.save(order);

			// commit the transaction
			session.getTransaction().commit();

			System.out.println("Order added successfully.");
		} catch (Exception e) {
			// rollback the transaction if there was an error
			session.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			// close the session and session factory
			session.close();
			sessionFactory.close();
		}
	}

	public void createOrder() throws SQLException, ClassNotFoundException {
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement stmt = connection.prepareStatement(
						"INSERT INTO 'order' (number, date, item, price) VALUES (?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS)) {

			connection.setAutoCommit(false);

			stmt.setInt(1, this.getNumber());
			stmt.setDate(2, new java.sql.Date(this.getDate().getTime()));
			stmt.setString(3, this.getItem());
			stmt.setDouble(4, this.getPrice());
			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating order failed, no rows affected.");
			}

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					this.setNumber(generatedKeys.getInt(1));
				} else {
					throw new SQLException("Creating order failed, no ID obtained.");
				}
			}

			connection.commit();
		}
	}

	// Search feature
	public Order searchOrder(int number) {
		Order order = new Order();
		SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Order.class)
				.buildSessionFactory();

		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			order = session.get(Order.class, number);
			session.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			factory.close();
		}
		if (order == null) {
			System.out.println("Order not found");
		} else {
			System.out.println("Found: " + order.getNumber());
		}
		return order;
	}

	// getters and setters

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customerID) {
		this.customer = customerID;
	}

	@Override
	public String toString() {
		return "Order: " + number + "," + date + "," + item + "," + price;
	}
}
