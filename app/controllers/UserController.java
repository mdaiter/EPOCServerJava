package controllers;

import java.awt.image.renderable.RenderableImage;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;

import net.sf.ehcache.hibernate.HibernateUtil;

import com.avaje.ebean.Ebean;

import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;

import play.data.*;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import models.ClientAppLog;
import models.User;
import models.UserApp;
import models.UserAppLogItem;

public class UserController extends Controller{

	final private static Form<UserApp> createAppForm = form(UserApp.class);

	public static Result index(){
		return ok(
			views.html.dev.userHome.render(User.search(session().get("email")), createAppForm, null)
		);
	}

	@play.db.ebean.Transactional
	public static Result attemptCreateNewApp(){
		Form<UserApp> tempForm = createAppForm.bindFromRequest();
		//User u = new User(User.search(session().get("email")).getLastName(), User.search(session().get("email")).getFirstName(),User.search(session().get("email")).getPassword(), User.search(session().get("email")).getUsername());

		if (tempForm.field("appNam").value() != "" 
				&& tempForm.field("appNam").value() != null
				&& tempForm.field("appNam").value() != " " ){
			return UserController.createNewApp(tempForm);
		}
		else{
			return forbidden(
					views.html.dev.userHome.render(User.search(session().get("email")), createAppForm, "You haven't filled anything out...")
					);
		}
	}
	
	@play.db.ebean.Transactional
	public static Result createNewApp(Form<UserApp> tempForm){
			System.out.println(tempForm.field("appNam").value());
			
			String appName = tempForm.field("appNam").value();
			UserApp newApp = new UserApp(appName);
			System.out.println("Saved app: " + UserApp.findAppWithUUID(appName) + "with table: " );
			
			System.out.println(User.search(session().get("email")).getFirstName() + User.search(session().get("email")).getLastName() + User.search(session().get("email")).getUsername() + User.search(session().get("email")).getPassword());
			
			if(User.search(session().get("email")).addValueToHash(newApp.getappUUID(), newApp)){
				System.out.println("User name: " + User.search(session().get("email")).getUsername() + " User firstname: " + User.search(session().get("email")).getPassword());
				
				System.out.println("This is the confirmation saved hash: " + User.search(User.search(session().get("email")).getUsername()).getUserAppHash() + " to user account " + User.search(session().get("email")).getFirstName());
								
				User.search(session().get("email")).save();
								
				System.out.println("This is the confirmation saved hash: " + User.search(User.search(session().get("email")).getUsername()).getUserAppHash() + " to user account " + User.search(session().get("email")).getFirstName());
				
				return successCreateNewApp();
				}
			else{
				return failedCreateNewApp();
			}		
		}
	
	@play.db.ebean.Transactional
	public static Result successCreateNewApp(){
		return ok(
				views.html.dev.newApp.render(User.search(session().get("email")))
			);
	}
	public static Result failedCreateNewApp(){
		return forbidden(
				views.html.dev.userHome.render(User.search(session().get("email")), createAppForm, "You've reached your size limit")
				);
	}
	
}