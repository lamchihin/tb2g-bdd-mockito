package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

    @Mock
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialitySDJpaService service;

    @Test
    void testDeleteByObject() {
        // given
        Speciality speciality = new Speciality();

        // when
        service.delete(speciality);

        // then
        then(specialtyRepository).should().delete(any(Speciality.class));
    }

    @Test
    void findByIdTest() {
        // given
        Speciality speciality = new Speciality();
        given(specialtyRepository.findById(1L)).willReturn(Optional.of(speciality));

        // when
        Speciality foundSpecialty = service.findById(1L);

        // then
        assertThat(foundSpecialty).isNotNull();
        verify(specialtyRepository).findById(anyLong());
        then(specialtyRepository).should(times(1)).findById(anyLong());
        // make sure specialtyRepository haa no other interaction
        then(specialtyRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteById() {
        // given - none

        // when
        service.deleteById(1l);
        service.deleteById(1l);

        // then
        then(specialtyRepository).should(times(2)).deleteById(1l);
    }

    @Test
    void deleteByIdAtLeast() {
        // given - none

        // when
        service.deleteById(1l);
        service.deleteById(1l);

        // then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1l);
    }

    @Test
    void deleteByIdAtMost() {
        // given - none

        // when
        service.deleteById(1l);
        service.deleteById(1l);

        // then
        then(specialtyRepository).should(atMost(5)).deleteById(1l);
    }

    @Test
    void deleteByIdNever() {
        // given - none

        // when
        service.deleteById(1l);
        service.deleteById(1l);

        // then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1l);
        then(specialtyRepository).should(never()).deleteById(5L);
    }

    @Test
    void testDelete() {
        // when
        service.delete(new Speciality());

        // then
        then(specialtyRepository).should().delete(any(Speciality.class));
    }

    @Test
    void testDoThrow() {
        // given - throw exception whenever delete is called
        doThrow(new RuntimeException("boom")).when(specialtyRepository).delete(any());

        // when & assertThrows
        assertThrows(RuntimeException.class, () -> specialtyRepository.delete(new Speciality()));

        // then
        verify(specialtyRepository).delete(any());
    }

    /*
        findById() returns something, so we can have given(...).findById(...).willThrow(...)
     */
    @Test
    void testFindByIDThrows() {
        // given
        given(specialtyRepository.findById(1L)).willThrow(new RuntimeException("boom"));

        // when
        assertThrows(RuntimeException.class, () -> service.findById(1L));

        // then
        then(specialtyRepository).should().findById(1L);
    }

    /*
        delete() returns void, so can't have given(...).delete(any()).willThrow(...)
        Trick here is willThrow(...).given(...).delete(any())
     */
    @Test
    void testDeleteBDD() {
        // given
        willThrow(new RuntimeException("boom")).given(specialtyRepository).delete(any());

        // when
        assertThrows(RuntimeException.class, () -> specialtyRepository.delete(new Speciality()));

        // then
        then(specialtyRepository).should().delete(any());
    }

    @Test
    void testSaveLambda() {
        // given
        final String MATCH_ME = "MATCH_ME";
        Speciality speciality = new Speciality();
        speciality.setDescription(MATCH_ME);

        Speciality savedSpecialty = new Speciality();
        savedSpecialty.setId(1L);

        // need mock to only return on match MATCH_ME string
        // when(specialtyRepository.save(speciality)).thenReturn(savedSpecialty);
        given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpecialty);

        // when
        Speciality returnedSpecialty = service.save(speciality);

        // then
        assertThat(returnedSpecialty.getId()).isEqualTo(1L);
    }
}