package homework;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class customerView extends Application {
	private TextField nameBox, phoneBox, emailBox, streetBox, stateBox, cityBox, zipBox;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage customer) {
		
		// Title label
		customer.setTitle("Customer");
				
		// Labels
		Label nameLabel = new Label("Name");
		Label phoneLabel = new Label("Phone");
		Label emailLabel = new Label("Email");
		Label streetLabel = new Label("Street");
		Label stateLabel = new Label("State");
		Label cityLabel = new Label("City");
		Label zipLabel = new Label("Zip Code");

		// Text fields
		nameBox = new TextField();
		phoneBox = new TextField();
		emailBox = new TextField();
        emailBox.setPrefSize(600, 10);
		streetBox = new TextField();
        streetBox.setPrefSize(250, 10);
		stateBox = new TextField();
        stateBox.setPrefSize(250, 10);
		cityBox = new TextField();
        cityBox.setPrefSize(250, 10);
		zipBox = new TextField();
        zipBox.setPrefSize(250, 10);

        Button search = new Button("Search");
        Button add = new Button("Add");
        Button update = new Button("Update");
        Button delete = new Button("Delete");

		// Combine each into their own VBox's
		VBox name = new VBox(10, nameLabel, nameBox);
		VBox phone = new VBox(10, phoneLabel, phoneBox);
		VBox email = new VBox(10, emailLabel, emailBox);
		VBox street = new VBox(10, streetLabel, streetBox);
		street.setPadding(new Insets(0, 50, 0, 0));
		VBox state = new VBox(10, stateLabel, stateBox);
		state.setPadding(new Insets(0, 50, 0, 0));
		VBox city = new VBox(10, cityLabel, cityBox);
		city.setPadding(new Insets(0, 0, 0, 50));
		VBox zip = new VBox(10, zipLabel, zipBox);
		zip.setPadding(new Insets(0, 0, 0, 50));
		
		// Hbox for the buttons
		HBox butt = new HBox(15, search, add, update, delete);
		butt.setAlignment(Pos.CENTER);
		butt.setPadding(new Insets(20));
		
		// Things on the same line
		HBox phomail = new HBox(10, phone, email);
		HBox strcity = new HBox(10, street, city);
		strcity.setAlignment(Pos.CENTER);
		HBox stazip = new HBox(10, state, zip);
		stazip.setAlignment(Pos.CENTER);

		// Separate categories
		VBox topObj = new VBox(20, name, phomail);
		topObj.setPadding(new Insets(10));
		VBox botObj = new VBox(10, strcity, stazip);
		botObj.setPadding(new Insets(10));
		botObj.setAlignment(Pos.CENTER);
		
		// Separate the categories
		Separator sep = new Separator();
        sep.setOrientation(Orientation.HORIZONTAL);
        sep.setPrefHeight(500);
        
        // BUTTON ACTION EVENTS
        search.setOnAction(e -> {
        	searchCustomer();
        });
        add.setOnAction(e -> {
        	addCustomer();
        });
        update.setOnAction(e -> {
        	updateCustomer();
        });
        delete.setOnAction(e -> {
        	delCustomer();
        });
        
        
		VBox root = new VBox(topObj, sep, botObj, butt);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(15));
		
		Scene customerView = new Scene(root, 700, 400);
		customer.setScene(customerView);
		customer.show();
	}
	
	private void addCustomer() {
        String name = nameBox.getText();
        String phone = phoneBox.getText();
        String email = emailBox.getText();
        String street = streetBox.getText();
        String state = stateBox.getText();
        String city = cityBox.getText();
        int zip = Integer.parseInt(zipBox.getText());;

		SessionFactory factory = new Configuration().
                configure("hibernate.cfg.xml").
                addAnnotatedClass(Customer.class).
                buildSessionFactory();

		Session session = factory.getCurrentSession();

		try {
			Customer customer = new Customer(name, phone, email);
			session.beginTransaction();
			session.save(customer);
			session.getTransaction().commit();
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		} 
		
		finally {
			factory.close();
		}

		// Reset the textfields
	    nameBox.setText("");
	    phoneBox.setText("");
	    emailBox.setText("");
	    streetBox.setText("");
	    stateBox.setText("");
	    cityBox.setText("");
	    zipBox.setText("");


		Stage popup = new Stage();
	    popup.initOwner(nameBox.getScene().getWindow());
	    popup.setTitle("Success");
	    Label label = new Label("Customer Successfully Included");
	    label.setAlignment(Pos.CENTER);
	    Scene scene = new Scene(label, 250, 100);
	    popup.setScene(scene);
	    popup.show();
	    
	}
	
	public void delCustomer() {
		String name = nameBox.getText();
		
		SessionFactory factory = new Configuration().
                configure("hibernate.cfg.xml").
                addAnnotatedClass(Customer.class).
                buildSessionFactory();

		Session session = factory.getCurrentSession();

		try {

			session.beginTransaction();
			Query query = session.createQuery("DELETE FROM Customer WHERE name = :name");
		    query.setParameter("name", name);
		    
		    int rowsDeleted = query.executeUpdate();

			session.getTransaction().commit();


			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				factory.close();
			}
		
		// Reset the textfields
	    nameBox.setText("");
	    phoneBox.setText("");
	    emailBox.setText("");
	    streetBox.setText("");
	    stateBox.setText("");
	    cityBox.setText("");
	    zipBox.setText("");
		
		Stage popup = new Stage();
	    popup.initOwner(nameBox.getScene().getWindow());
	    popup.setTitle("Success");
	    
	    Label label = new Label("Customer Successfully Deleted");
	    label.setAlignment(Pos.CENTER);
	    
	    Scene scene = new Scene(label, 250, 100);
	    popup.setScene(scene);
	    popup.show();
	}
	
	public List<Customer> searchCustomer() {
		String name = nameBox.getText()
;		List<Customer> customers;

		SessionFactory factory = new Configuration().
                configure("hibernate.cfg.xml").
                addAnnotatedClass(Customer.class).
                buildSessionFactory();

		Session session = factory.getCurrentSession();

		

			session.beginTransaction();

			Query query = session.createQuery("FROM Customer WHERE name LIKE :name");
		    query.setParameter("name", "%" + name + "%");
		    customers = query.getResultList();
		    
			session.getTransaction().commit();
			
			if (customers.isEmpty()) {
				Stage popup = new Stage();
			    popup.initOwner(nameBox.getScene().getWindow());
			    popup.setTitle("Customer Not Found");
			    
			    Label label = new Label("No Records Found");
			    label.setAlignment(Pos.CENTER);
			    
			    Scene scene = new Scene(label, 250, 100);
			    popup.setScene(scene);
			    popup.show();  
		    } 
			
			else {
		        Customer customer = customers.get(0);
		        nameBox.setText(customer.getName());
		        phoneBox.setText(customer.getPhone());
		        emailBox.setText(customer.getEmail());
		    }
			return customers;	
	}
	
	public void updateCustomer() {
		String name = nameBox.getText();
        String phone = phoneBox.getText();
        String email = emailBox.getText();
        String street = streetBox.getText();
        String state = stateBox.getText();
        String city = cityBox.getText();
        int zip = Integer.parseInt(zipBox.getText());;
        
     // Search for a customer matching the name
        List<Customer> customers = searchCustomer();
        
     // If a matching customer was found, update its data
        if (!customers.isEmpty()) {
            Customer customer = customers.get(0);
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);

         // Save the updated customer data to the database
            SessionFactory factory = new Configuration().
                    configure("hibernate.cfg.xml").
                    addAnnotatedClass(Customer.class).
                    buildSessionFactory();

    		Session session = factory.getCurrentSession();

    			session.beginTransaction();
    			
    			session.update(customer);
    			
				session.getTransaction().commit();
		            
				// Reset the textfields
			    nameBox.setText("");
			    phoneBox.setText("");
			    emailBox.setText("");
			    streetBox.setText("");
			    stateBox.setText("");
			    cityBox.setText("");
			    zipBox.setText("");
			    
				Stage popup = new Stage();
			    popup.initOwner(nameBox.getScene().getWindow());
			    popup.setTitle("Customer Updated");
			    
			    Label label = new Label("Customer Data Successfully Updated");
			    label.setAlignment(Pos.CENTER);
			    
			    Scene scene = new Scene(label, 250, 100);
			    popup.setScene(scene);
			    popup.show();
        }
	}
}	

