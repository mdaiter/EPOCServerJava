package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import play.data.*;
import play.db.ebean.Transactional;

import models.mainModel;
import models.User;

public class NewApp extends Controller{
	
	//@Transactional
	public static Result index(){
		return ok(
				views.html.dev.newApp.render(User.search(session().get("email")))
			);
	}
}