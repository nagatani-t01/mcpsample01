/*
<<<<<<< HEAD
 * Copyright 2012-2025 the original author or authors.
=======
 * Copyright 2002-2013 the original author or authors.
>>>>>>> react-port
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
<<<<<<< HEAD
 *      https://www.apache.org/licenses/LICENSE-2.0
=======
 *      http://www.apache.org/licenses/LICENSE-2.0
>>>>>>> react-port
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.model;

<<<<<<< HEAD
import java.io.Serializable;

=======
>>>>>>> react-port
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

<<<<<<< HEAD
/**
 * Simple JavaBean domain object with an id property. Used as a base class for objects
 * needing this property.
=======
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple JavaBean domain object with an id property. Used as a base class for objects needing this property.
>>>>>>> react-port
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass
<<<<<<< HEAD
public class BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isNew() {
		return this.id == null;
	}
=======
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @JsonIgnore
    public boolean isNew() {
        return this.id == null;
    }
>>>>>>> react-port

}
