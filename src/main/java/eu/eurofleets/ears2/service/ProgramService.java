package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.program.Program;
import eu.eurofleets.ears2.domain.program.Project;
import java.util.List;

public abstract interface ProgramService
{
  public abstract List<Program> getProgramList();
  
  public abstract Program getProgramByID(String paramString);
  
  public abstract List<Program> getProgramListByCruiseId(String paramString);
  
  public abstract void setProgram(Program paramProgram);
  
  public abstract void setProgram(String paramString, Program paramProgram);
  
  public abstract void removeProgram(String paramString);
  
  public abstract Project getProject(String paramString);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/ProgramService.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */