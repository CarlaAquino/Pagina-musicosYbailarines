package edu.team5.finalproject.service;

import edu.team5.finalproject.dto.ContactDto;
import edu.team5.finalproject.entity.Contact;
import edu.team5.finalproject.exception.ExceptionMessages;
import edu.team5.finalproject.exception.MyException;
import edu.team5.finalproject.mapper.GenericModelMapper;
import edu.team5.finalproject.repository.ContactRepository;
import edu.team5.finalproject.utility.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {

   private final GenericModelMapper mapper;
   private final ContactRepository contactRepository;

   @Transactional
   public void create(ContactDto dto) throws MyException {
      validateContact(dto); 

      Contact contact = mapper.map(dto, Contact.class);         
      
      contact.setDeleted(false);
      contactRepository.save(contact);
   }

   @Transactional
   public void update(ContactDto dto) throws MyException {
      validateContactUpdate(dto); 

      Contact contact = mapper.map(dto, Contact.class);     
  
      contactRepository.save(contact);
   }

   @Transactional
   public Contact getById(Long id) {
      return contactRepository.findById(id).get();
   }

   @Transactional
   public void updateEnableById(Long id) {
       contactRepository.enableById(id);
   }

   @Transactional
   public void deleteById(Long id) {
      contactRepository.deleteById(id);
   }

   private void validateContact(ContactDto dto) throws MyException {
      if(!Utility.validate(Utility.ONLY_NUMBERS_PATTERN, dto.getContactWhatsAppNumber().toString())) 
         throw new MyException(ExceptionMessages.INVALID_NUMBER.get()); 

      if (contactRepository.existsByWhatsAppNumber(dto.getContactWhatsAppNumber())) 
         throw new MyException(ExceptionMessages.ALREADY_EXISTS_WHATSAPP_NUMBER.get());
   }

   private void validateContactUpdate(ContactDto dto) throws MyException {
      if(!Utility.validate(Utility.ONLY_NUMBERS_PATTERN, dto.getContactWhatsAppNumber().toString())) 
         throw new MyException(ExceptionMessages.INVALID_NUMBER.get()); 
   }

}
