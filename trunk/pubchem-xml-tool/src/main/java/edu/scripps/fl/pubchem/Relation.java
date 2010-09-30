package edu.scripps.fl.pubchem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name = "relation")
@org.hibernate.annotations.Table(appliesTo = "relation", indexes = {
		@Index(name = "idx_relation_1", columnNames = { "fromId", "fromDb" })
})
public class Relation {

	
	private Long id;
	private Long fromId;
	private String fromDb;
	private Long toId;
	private String toDb;
	private String relationName;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "fromId")
	public Long getFromId() {
		return fromId;
	}

	public void setFromId(Long fromId) {
		this.fromId = fromId;
	}

	@Column(name = "fromDb")
	@Index(name = "idx_relation_fromdb")
	public String getFromDb() {
		return fromDb;
	}

	public void setFromDb(String fromDb) {
		this.fromDb = fromDb;
	}

	@Column(name = "toId")
	public Long getToId() {
		return toId;
	}

	public void setToId(Long toId) {
		this.toId = toId;
	}

	@Column(name = "toDb")
	@Index(name = "idx_relation_todb")
	public String getToDb() {
		return toDb;
	}

	public void setToDb(String toDb) {
		this.toDb = toDb;
	}

	@Index(name = "idx_relation_relname")
	@Column(name = "relation_name")
	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this);
	}
}
