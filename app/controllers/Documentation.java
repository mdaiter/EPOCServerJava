package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import models.User;

public class Documentation extends Controller{
	public static Result index(){
		return ok(
				views.html.docs.index.render(User.search(session().get("email")))
			);
	}
}
