package homework;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
//import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
//import javax.persistence.JoinColumn;
//import javax.persistence.CascadeType;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import jdbc.ConnectionFactory;

import homework.Address;
import homework.Order;

@Entity
@Table(name = "customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private int customerID;
	@Column(name = "name")
	private String name;
	@Column(name = "phone")
	private String phone;
	@Column(name = "email")
	private String email;

	@OneToOne
	@JoinColumn(name = "address_id")
	private Address address;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Order> orders;

	public Customer(String name, String phone, String email) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.orders = new ArrayList<Order>();
	}

	public Customer() {
		super();
	}

	public static void main(String[] args) {
		Customer customer = new Customer("John Doe", "555-1234", "john.doe@example.com");
		Address address = new Address("123 Main St", "Anytown", "CA", 12345);
		customer.setAddress(address);

		try {
			// create a new customer
			customer.createCustomer();
			System.out.println("Customer created with ID: " + customer.getId());

			// search for a customer
			Customer foundCustomer = customer.searchCust(customer.getId());
			System.out.println("Found customer: " + foundCustomer);

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void createCustomer() throws ClassNotFoundException, SQLException {
		Connection connection = ConnectionFactory.getConnection();

		connection.setAutoCommit(false);

		PreparedStatement stmt = connection.prepareStatement("INSERT INTO customer(name, phone, email) VALUES (?,?,?)");
		stmt.setString(1, this.getName());
		stmt.setString(2, this.getPhone());
		stmt.setString(3, this.getEmail());
		stmt.executeUpdate();

		stmt = connection.prepareStatement("SELECT * FROM customer order by customer_id desc");

		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			this.setId(rs.getInt("customer_id"));
		}
		connection.commit();
	}

	// Search feature
	public Customer searchCust(int customerID) {
		Customer cust = new Customer();
		SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Customer.class)
				.buildSessionFactory();

		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			cust = session.get(Customer.class, customerID);
			session.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			factory.close();
		}
		if (cust == null)
			System.out.println("Customer not found");
		else
			System.out.println("Found: " + cust.getId());

		return cust;
	}

	// getters and setters
	public int getId() {
		return customerID;
	}

	public void setId(int id) {
		this.customerID = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
	}

	@Override
	public String toString() {
		return "Customer: " + name + "," + email + "," + phone;
	}
}
