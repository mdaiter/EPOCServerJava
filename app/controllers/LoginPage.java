package controllers;

import javax.persistence.PersistenceException;

import play.mvc.Http.Session;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import play.data.*;

import models.User;

public class LoginPage extends Controller{

	final static Form<User> loginForm = form(User.class);

	public static Result index(){
		return ok(
			views.html.dev.login.render(loginForm, null, User.search(session().get("email")))
			);
	}

	public static Result login(){
		Form<User> tempForm = loginForm.bindFromRequest();
		User tempU = null;
		try{
			tempU = User.search(tempForm.field("username").value());
			System.out.println("Ended database search with value: " + tempU);
			System.out.println("Username: " + tempU.getUsername());
			System.out.println("Password: " + tempU.getPassword());
			System.out.println("First name: " + tempU.getFirstName());
			System.out.println("Last name: " + tempU.getLastName());
			System.out.println("Hash map: " + tempU.getUserAppHash());
		}
		catch (PersistenceException e){
			System.out.println("Search unsuccessful");
			return unauthorized(
					views.html.dev.login.render(loginForm, "Email non-existant. Try again.", User.search(session().get("email")))
				);
 
		}	
			if (!tempU.getPassword().equals( tempForm.field("password").value())){
				System.out.println("Field password is: " + tempForm.field("password").value());
				System.out.println("Username password: " + tempU.getPassword());
				return unauthorized(
						views.html.dev.login.render(loginForm, "Incorrect password. Try again.", User.search(session().get("email")))
					);
			}
			else{

				session("email", tempU.getUsername());
				return redirect(
						routes.Application.index()
					);
			}
		
	}

	public static Result logout(){
		System.out.println("Starting logout of: " + session().get("email"));
		session().clear();
		flash("success", "You've been logged out.");
		return redirect(
				routes.Application.index()
			);
	}

}