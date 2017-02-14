package org.vaadin.stepbystep.contacts;

import java.time.ZoneId;
import java.util.Date;

import org.vaadin.stepbystep.person.backend.Person;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.server.ExternalResource;

public class PersonView extends PersonDesign {

	public interface PersonSaveListener {
		void savePerson(Person person);
	}

	public interface PersonDeleteListener {
		void deletePerson(Person person);
	}

	Binder<Person> binder = new Binder<>(Person.class);

	private Person editedPerson;

	public PersonView(PersonSaveListener saveEvt, PersonDeleteListener delEvt) {
		binder.forMemberField(dateOfBirth).withConverter(new LocalDateToDateConverter());
		binder.bindInstanceFields(this);

		save.addClickListener(evt -> {
			try {
				binder.writeBean(editedPerson);
				saveEvt.savePerson(editedPerson);
			} catch (ValidationException e) {
				e.printStackTrace();
			}
		});

		cancel.addClickListener(evt -> {
			binder.readBean(editedPerson);
		});

		delete.addClickListener(evt -> {
			delEvt.deletePerson(editedPerson);
		});
	}

	public void setPerson(Person selectedRow) {
		this.editedPerson = selectedRow;
		binder.readBean(selectedRow);

		picture.setSource(new ExternalResource(selectedRow.getPicture()));
	}

}
