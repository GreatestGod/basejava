package ru.javawebinar.basejava.storage.serializer;

import ru.javawebinar.basejava.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStreamSerializer implements StreamSerializer {

    @Override
    public void doWrite(Resume r, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());
            Map<ContactType, String> contacts = r.getContacts();
            dos.writeInt(contacts.size());
            for (Map.Entry<ContactType, String> entry : contacts.entrySet()) {
                dos.writeUTF(entry.getKey().name());
                dos.writeUTF(entry.getValue());
            }
            Map<SectionType, Section> sections = r.getSections();
            dos.writeInt(sections.size());
            for (Map.Entry<SectionType, Section> entry : sections.entrySet()) {
                dos.writeUTF(entry.getKey().name());
                Section section = entry.getValue();
                if (section instanceof TextSection) {
                    dos.writeUTF("TextSection");
                    TextSection textSection = (TextSection) section;
                    dos.writeUTF(textSection.getContent());
                } else if (section instanceof ListSection){
                    dos.writeUTF("ListSection");
                    ListSection listSection = (ListSection) section;
                    List<String> records = listSection.getItems();
                    dos.writeInt(records.size());
                    for (String str : listSection.getItems()) {
                        dos.writeUTF(str);
                    }
                } else {
                    dos.writeUTF("OrganizationSection");
                    List<Organization> organizations = ((OrganizationSection) section).getOrganizations();
                    dos.writeInt(organizations.size());
                    for (Organization organization : organizations) {
                        dos.writeUTF(organization.getHomePage().getName());
                        String url = organization.getHomePage().getUrl();
                        if (url != null) {
                            dos.writeUTF(organization.getHomePage().getUrl());
                        } else {
                            dos.writeUTF("null");
                        }
                        List<Organization.Position> positions = organization.getPositions();
                        dos.writeInt(positions.size());
                        for (Organization.Position position : positions) {
                            String description = position.getDescription();
                            if (description != null) {
                                dos.writeUTF(position.getDescription());
                            } else {
                                dos.writeUTF("null");
                            }
                            dos.writeUTF(position.getTitle());
                            dos.writeUTF(position.getEndDate().toString());
                            dos.writeUTF(position.getStartDate().toString());
                        }
                    }
                }
            }
        }
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        try (DataInputStream dis = new DataInputStream(is)) {
            String uuid = dis.readUTF();
            String fullName = dis.readUTF();
            Resume resume = new Resume(uuid, fullName);
            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                resume.addContact(ContactType.valueOf(dis.readUTF()), dis.readUTF());
            }
            int sectionTypeSize = dis.readInt();
            for (int i = 0; i < sectionTypeSize; i++) {
                SectionType sectionType = SectionType.valueOf(dis.readUTF());
                String sectionInstance = dis.readUTF();
                if (sectionInstance.equals("TextSection")) {
                    resume.addSection(sectionType, new TextSection(dis.readUTF()));
                } else if (sectionInstance.equals("ListSection")){
                    int listSize = dis.readInt();
                    List<String> records = new ArrayList<>();
                    for (int j = 0; j < listSize; j++) {
                        records.add(dis.readUTF());
                    }
                    resume.addSection(sectionType, new ListSection(records));
                } else {
                    int organisationListSize = dis.readInt();
                    List<Organization> organizations = new ArrayList<>();
                    for (int j = 0; j < organisationListSize; j++) {
                        String name = dis.readUTF();
                        String url = dis.readUTF();
                        int positionListSize = dis.readInt();
                        List<Organization.Position> positions = new ArrayList<>();
                        for (int k = 0; k < positionListSize; k++) {
                            String description = dis.readUTF();
                            String title = dis.readUTF();
                            LocalDate endDate = LocalDate.parse(dis.readUTF());
                            LocalDate startDate = LocalDate.parse(dis.readUTF());
                            if (!description.equals("null")) {
                                positions.add(new Organization.Position(startDate, endDate, title, description));
                            } else {
                                positions.add(new Organization.Position(startDate, endDate, title, null));
                            }
                        }
                        if (!url.equals("null")) {
                            organizations.add(new Organization(new Link(name, url), positions));
                        } else {
                            organizations.add(new Organization(new Link(name, null), positions));
                        }
                    }
                    resume.addSection(sectionType, new OrganizationSection(organizations));
                }
            }
            return resume;
        }
    }
}
