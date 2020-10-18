package com.example.holidayservice;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HolidayServiceApplicationTests {

  @Autowired
  private MockMvc mvc;

  @Test
  public void shouldFindNextHoliday() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-10-10")
        .param("country1", "PR")
        .param("country2", "PL"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nextHolidayDate", is("2020-11-11")))
        .andExpect(jsonPath("$.holidayName1", is("Día del Veterano Día del Armisticio")))
        .andExpect(jsonPath("$.holidayName2", is("Narodowe Święto Niepodległości")));
  }

  @Test
  public void shouldFindNextHolidayInNextYear() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-12-25")
        .param("country1", "PL")
        .param("country2", "GB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nextHolidayDate", is("2021-01-01")))
        .andExpect(jsonPath("$.holidayName1", is("Nowy Rok")))
        .andExpect(jsonPath("$.holidayName2", is("New Year's Day")));
  }

  @Test
  public void shouldThrowNotSupportedCountryCode() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-10-10")
        .param("country1", "AQ")
        .param("country2", "PL"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorMessage", is("Country code: AQ is not supported. ")))
        .andExpect(jsonPath("$.supportedCountries[0].key", is("AD")));
  }

  @Test
  public void shouldFindNextHolidayWhenTheSameCountries() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-04-20")
        .param("country1", "PL")
        .param("country2", "PL"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nextHolidayDate", is("2020-05-01")))
        .andExpect(jsonPath("$.holidayName1", is("Święto Pracy")))
        .andExpect(jsonPath("$.holidayName2", is("Święto Pracy")));
  }

  @Test
  public void shouldFindNextHolidayWhenSendDateWithHoliday() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-05-01")
        .param("country1", "PL")
        .param("country2", "DE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nextHolidayDate", is("2020-06-11")))
        .andExpect(jsonPath("$.holidayName1", is("Boże Ciało")))
        .andExpect(jsonPath("$.holidayName2", is("Fronleichnam")));
  }

  @Test
  public void shouldFindNextHolidayWhenCountryHaveMoreThanOneHolidayInDate() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-09-01")
        .param("country1", "CN")
        .param("country2", "PL"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nextHolidayDate", is("2021-01-01")))
        .andExpect(jsonPath("$.holidayName1", is("元旦")))
        .andExpect(jsonPath("$.holidayName2", is("Nowy Rok")));
  }

  @Test
  public void shouldFindNextHolidayNamesWhenCountryHaveMoreThanOneHolidayInDate() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-09-01")
        .param("country1", "CN")
        .param("country2", "CY"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nextHolidayDate", is("2020-10-01")))
        .andExpect(jsonPath("$.holidayName1", is("中秋节 | 国庆节")))
        .andExpect(jsonPath("$.holidayName2",
            is("Επέτειος Κυπριακής Ανεξαρτησίας")));
  }

  @Test
  public void shouldThrowErrorWhenDateParamOmit() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("country1", "CN")
        .param("country2", "CY"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorMessage",
            is("Required LocalDate parameter 'date' is not present")));
  }

  @Test
  public void shouldThrowErrorWhenCountry1ParamOmit() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-09-01")
        .param("country2", "CY"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorMessage",
            is("Required String parameter 'country1' is not present")));
  }

  @Test
  public void shouldThrowErrorWhenCountry2ParamOmit() throws Exception {
    mvc.perform(get("/api/holidays/next-holiday")
        .param("date", "2020-09-01")
        .param("country1", "CN"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorMessage",
            is("Required String parameter 'country2' is not present")));
  }
}
