package org.vaadin.stepbystep.contacts;

import java.time.ZoneId;
import java.util.Date;

import org.vaadin.stepbystep.person.backend.Person;

import com.vaadin.data.BeanBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.server.ExternalResource;

public class PersonView extends PersonDesign {

	public interface PersonSaveListener {
		void savePerson(Person person);
	}

	public interface PersonDeleteListener {
		void deletePerson(Person person);
	}

	BeanBinder<Person> binder = new BeanBinder<>(Person.class);

	private Person editedPerson;

	public PersonView(PersonSaveListener saveEvt, PersonDeleteListener delEvt) {
		// Should be a built-in converter for this
		binder.forMemberField(dateOfBirth).withConverter(
				localDate -> Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
				// Must wrap as a new Date since sql.Date doesn't support toInstant()
				date -> new Date(date.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
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
