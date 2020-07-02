 package eu.eurofleets.ears3.Exceptions;
 
 import org.springframework.dao.DuplicateKeyException;
 
 public class DuplicateIdException extends DuplicateKeyException
 {
   private static final long serialVersionUID = 1L;
   
   public DuplicateIdException(String msg) {
     super(msg);
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu.eurofleets.ears3/Exceptions/DuplicateIdException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */