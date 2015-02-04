/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.sdc.puppetwrapper.data;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Java Class to manage the attribute of a component.
 * @author Albert Sinfreu Alay
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Attribute {

    private Long id;
    private Long v;
    private String key;
    private String value;
    private String description;


    /**
     * Constructor.
     */
    public Attribute() {
    }

    /**
     * @param key
     * @param value
     */
    public Attribute(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @param key
     * @param value
     * @param description
     */
    public Attribute(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Return the string corresponding to the attribute.
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[Attribute]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[v = ").append(this.v).append("]");
        sb.append("[key = ").append(this.key).append("]");
        sb.append("[value = ").append(this.value).append("]");
        sb.append("[description = ").append(this.description).append("]");
        sb.append("]");
        return sb.toString();
    }


}
