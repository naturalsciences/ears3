package eu.eurofleets.ears2.domain.cruise;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;

public @interface SSRSchema
{
  String namespace();
  
  XmlNsForm elementFormDefault();
  
  XmlNs[] xmlns();
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/cruise/SSRSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */