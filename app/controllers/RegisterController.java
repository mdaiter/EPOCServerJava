package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import play.data.*;
import play.db.jpa.JPA;

import models.mainModel;
import models.User;

public class RegisterController extends Controller{

	final static Form<User> signupForm = form(User.class);

	/*
	If it's just a get, return a signup form
	*/
	
	//@play.db.ebean.Transactional
	public static Result index(){
		return ok(
			views.html.dev.register.render(signupForm, User.search(session().get("email")))
		);
	}

	@play.db.ebean.Transactional
	public static Result register(){
		Form<User> tempForm = signupForm.bindFromRequest();

		//Instantiate a new user with the DATA
		User u = new User(tempForm.field("lastName").value(), tempForm.field("firstName").value(), tempForm.field("password").value(), tempForm.field("username").value());
		
		System.out.println("Temp form is equal to: " + tempForm);
		
		if (!tempForm.field("password").valueOr("").isEmpty()){
			System.out.println("Password not empty");
			if (!tempForm.field("password").valueOr("").equals(tempForm.field("repeatPassword").value())){
				tempForm.reject("repeatPassword", "Passwords don't match");
			}
		}

//		if (!tempForm.hasErrors()){
//			System.out.println("Form has no errors");
//			if (tempForm.get().getUsername().equals("admin@*.com") || tempForm.get().getUsername().equals("guest")){
//				tempForm.reject("username", "This username has already been taken");
//			}
//		}
//
//		if (tempForm.hasErrors()){
//			System.out.println("Form has errors. Reloading page.");
//			System.out.println("Here are the errors: " + tempForm.errors().values());
//			 return badRequest(views.html.dev.register.render(tempForm));
//		}
		 //else{
			 System.out.println("Form doesn't have errors!");
			 u.save();
			System.out.println("We just created: " + u);
			return ok(views.html.dev.registerSuccess.render(u));
	//	 }
	}

}