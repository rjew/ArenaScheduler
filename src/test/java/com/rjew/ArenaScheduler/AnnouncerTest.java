package com.rjew.ArenaScheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ScannerUtils.class)
@PowerMockIgnore("javax.management.*")
public class AnnouncerTest {
    private Announcer announcer;

    @Before
    public void setUp() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        announcer = new Announcer();

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

        //Suppress System.out.print
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                //No Output
            }
        }));
    }

    /* Cannot run at same time as other test methods because would cause more than one
    /* instance of derby database connection to be accessed
    /*
    /* @PrepareForTest(DAOUtils.class)
    /* @Test
    /* public void printFullAnnouncerShouldPrintCourseList() throws SQLException {
    /*     PowerMockito.mockStatic(DAOUtils.class);
    /*
    /*     announcer.printFullAnnouncer();
    /*
    /*     PowerMockito.verifyStatic(times(1));
    /*     DAOUtils.printCourseList(anyList());
    /* }
     */

    @Test
    public void runSearchCatalogWithCorrectSearchParameterShouldReturnNumberResults() throws SQLException {
        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getInt()).thenReturn(2);
        PowerMockito.when(ScannerUtils.getString()).thenReturn("ALGC151A");

        assertEquals(16, announcer.runSearchCatalog());
    }

    @Test
    public void runSearchCatalogWithIncorrectSearchParameterShouldReturnZeroResults() throws SQLException {
        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getInt()).thenReturn(2);
        PowerMockito.when(ScannerUtils.getString()).thenReturn("qwerty");

        assertEquals(0, announcer.runSearchCatalog());
    }

    @Test
    public void getCourseWithValidClassIDShouldReturnCorrectCourse() throws SQLException {
        PowerMockito.mockStatic(ScannerUtils.class);
        PowerMockito.when(ScannerUtils.getInt()).thenReturn(1);

        Course course = new Course();
        course.setSubjectID(1);
        course.setCourseID("ALGC151A");
        course.setCourseTitle("Algebra 1A");
        course.setClassID(1);
        course.setSeats(32);
        course.setCode('B');
        course.setBlock(1);
        course.setRoom("256");
        course.setTeacher("Hong");

        assertEquals(course, announcer.getCourse());
    }

    @Test
    public void getCourseWithInvalidClassIDShouldReturnEmptyCourse() throws SQLException {
        PowerMockito.mockStatic(ScannerUtils.class);
        PowerMockito.when(ScannerUtils.getInt()).thenReturn(9999);

        assertEquals(new Course(), announcer.getCourse());
    }
}