package controllers;

import models.User;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

	public static Result index() {
		

		
		return ok(
				views.html.index.render(User.search(session().get("email")))
		);
	}
	
	public static Result index(User x){
		return ok(
				views.html.index.render(x)
			);
	}
	
	public static Result about(){
		return ok(
				views.html.about.render(User.search(session().get("email")))
			);
	}
}
