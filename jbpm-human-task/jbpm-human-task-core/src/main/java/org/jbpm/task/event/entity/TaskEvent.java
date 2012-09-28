/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task.event.entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@SequenceGenerator(name="taskEventIdSeq", sequenceName="TASK_EVENT_ID_SEQ", allocationSize=1)
public abstract class TaskEvent implements Externalizable {
	
    @Column(insertable=false, updatable=false)
    private String type;    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="taskEventIdSeq")
    private Long id;
    
    @Column(nullable=false)
    protected Long taskId;
    
    @Temporal(TemporalType.TIMESTAMP)
    protected Date eventTime = new Date();

    @Column(nullable=true)
    protected String userId;
    
    @Column(nullable=true)
    protected int sessionId;
    
    public TaskEvent() { 
        // Default constructor
    }
    
    public TaskEvent(long taskId, int sessionId) {
        this.taskId = taskId;
        this.sessionId = sessionId;
    }
    
    public TaskEvent(long taskId, String userId, int sessionId) {
        this.taskId = taskId;
        this.userId = userId;
        this.sessionId = sessionId;
    }
    
    public final void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(taskId != null ? taskId : -1);

        // type is a purely persistence related field and not necessary in other places
        out.writeLong(id != null ? id : -1);
        out.writeLong(eventTime != null ? eventTime.getTime() : -1);
        
        // forwards compatibility : when we need to add more fields, we update version and read logic
        short version = 0;
        out.writeShort(version);
        
        // where future fields should be written
        out.writeInt(sessionId);
    }  
    
    public final void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long readLong = in.readLong();
        taskId = (readLong > -1l) ? new Long(readLong) : null;
        
        readLong = in.readLong();
        id = (readLong > -1l) ? new Long(readLong) : null;
        readLong = in.readLong();
        eventTime = (readLong > -1l) ? new Date(readLong) : null;
        
        // forwards compatibility
        short version = in.readShort();
        
        // where future fields should be read, depending on version num
        this.sessionId = in.readInt();
    }
    
    public long getTaskId() {
        return taskId;
    }

    // no setter, automatically generated by db
    public Long getId() {
        return id;
    }

    // no setter, 
    public Date getEventTime() {
        return eventTime;
    }

    public String getType() { 
        return type;
    }
    
    public TaskEventType getTaskEventType() { 
        return TaskEventType.getTypeFromValue(type);
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

}
