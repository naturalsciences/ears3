/*
 * To change this license header, choose License Headers in ProjectBean Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain.ears2;

import eu.eurofleets.ears3.domain.Project;
import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yvan Stojanov
 */
@XmlRootElement(namespace = "http://www.eurofleets.eu/", name = "projects")
public class ProjectBean implements Serializable, Comparable<ProjectBean> {

    private Project project;

    public ProjectBean() {

    }

    public ProjectBean(Project project) {
        this.project = project;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return project.getTerm().getUrn();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "projectName")
    public String getName() {
        return project.getTerm().getName();
    }

    @Override
    public String toString() {
        return this.project.toString();
    }

    @Override
    public int compareTo(ProjectBean other) {
        if (this.getId() == null) {
            return 0;
        } else {
            return this.getId().compareTo(other.getId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof ProjectBean) {
            ProjectBean other = (ProjectBean) o;
            if (this.getId() != null) {
                return this.getId().equals(other.getId());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(getId());
        return hash;
    }
}
