package com.rjew.ArenaScheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ScannerUtils.class)
public class CustomScheduleManagerTest {
    private CustomScheduleManager customScheduleManager;

    @Before
    public void setUp() {
        List<Schedule> scheduleList = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            Schedule schedule = new Schedule();
            schedule.setName("Schedule " + i);
            schedule.setRank(i);

            List<Course> courseList = new ArrayList<>();
            for (int j = 1; j < 6; j++) {
                Course course = new Course();
                course.setBlock(j);
                course.setClassID(j);
                courseList.add(course);
            }

            schedule.setCourseList(courseList);
            scheduleList.add(schedule);
        }

        //5 Schedules with different schedule names and ranks, but with identical course lists of 5 courses
        customScheduleManager = new CustomScheduleManager(scheduleList);

        //Suppress System.out.print
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                //No Output
            }
        }));
    }

    @Test
    public void emptyScheduleListForGetScheduleNameShouldReturnNegativeOne() {
        CustomScheduleManager emptyCustomScheduleManager = new CustomScheduleManager();

        assertEquals(-1, emptyCustomScheduleManager.getScheduleName(""));
    }

    @Test
    public void getScheduleNamesShouldReturnCorrectIndex() {
        PowerMockito.mockStatic(ScannerUtils.class);

        when(ScannerUtils.getInt()).thenReturn(2);

        assertEquals(1, customScheduleManager.getScheduleName(""));
    }

    @Test
    public void emptyScheduleListShouldCallAddCourse() throws Exception {
        CustomScheduleManager customScheduleManagerSpy = PowerMockito.spy(new CustomScheduleManager());

        Course course = new Course();
        course.setClassID(1);
        course.setBlock(1);

        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getString()).thenReturn("Schedule 1");

        customScheduleManagerSpy.saveCourse(course);

        PowerMockito.verifyPrivate(customScheduleManagerSpy, times(1)).invoke("addCourse", course);
    }

    @Test
    public void correctScheduleSelectShouldAddCourse() {
        Course courseMock = mock(Course.class);

        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getInt()).thenReturn(2);

        customScheduleManager.saveCourse(courseMock);

        verify(courseMock).getClassID();
    }

    @Test
    public void createNewScheduleOptionShouldCallAddCourse() throws Exception {
        CustomScheduleManager customScheduleManagerSpy = PowerMockito.spy(customScheduleManager);

        Course course = new Course();
        course.setClassID(1);
        course.setBlock(1);

        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getInt()).thenReturn(6);

        customScheduleManagerSpy.saveCourse(course);

        PowerMockito.verifyPrivate(customScheduleManagerSpy, times(1)).invoke("addCourse", course);
    }

    @Test
    public void addCourseAddsSuccessfully() throws Exception {
        Course courseMock = mock(Course.class);

        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getString()).thenReturn("Schedule 8");

        Whitebox.invokeMethod(customScheduleManager, "addCourse", courseMock);

        assertTrue((boolean) Whitebox.invokeMethod(customScheduleManager, "checkScheduleExists", "Schedule 8"));

        verify(courseMock).getClassID();
    }

    @Test
    public void notProperScheduleShouldNotDeleteCourse() {
        CustomScheduleManager customScheduleManagerMock = mock(CustomScheduleManager.class);

        when(customScheduleManagerMock.viewSchedule(1)).thenReturn(false);

        customScheduleManagerMock.deleteCourse(1);

        verify(customScheduleManagerMock, times(0)).viewSchedule(1);
    }

    @Test
    public void classIDValidShouldDeleteCourse() {
        CustomScheduleManager customScheduleManagerSpy = spy(customScheduleManager);

        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getInt()).thenReturn(1);

        customScheduleManagerSpy.deleteCourse(0);

        verify(customScheduleManagerSpy, times(2)).viewSchedule(0);
    }

    @Test
    public void createScheduleShouldCreateCorrectSchedule() {
        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getString()).thenReturn("Schedule 99");

        assertEquals("Schedule 99", customScheduleManager.createSchedule());
    }

    @Test
    public void renameScheduleShouldSetName() {
        Schedule scheduleSpy = spy(new Schedule("Schedule 1"));

        List<Schedule> scheduleList = new ArrayList<>();

        scheduleList.add(scheduleSpy);

        CustomScheduleManager customScheduleManager1 = new CustomScheduleManager(scheduleList);

        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getString()).thenReturn("Schedule 2");

        customScheduleManager1.renameSchedule(0);

        verify(scheduleSpy).setName("Schedule 2");
    }

    @Test
    public void duplicateScheduleShouldAddCopy() {
        Schedule scheduleSpy = spy(new Schedule("Schedule 1"));

        List<Schedule> scheduleList = new ArrayList<>();

        scheduleList.add(scheduleSpy);

        CustomScheduleManager customScheduleManager1 = new CustomScheduleManager(scheduleList);

        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getString()).thenReturn("Schedule 2");

        customScheduleManager1.duplicateSchedule(0);

        verify(scheduleSpy, times(2)).getName();
    }

    @Test
    public void nonEmptyScheduleViewScheduleShouldReturnTrue() {
        assertTrue(customScheduleManager.viewSchedule(0));
    }

    @Test
    public void emptyScheduleViewScheduleShouldReturnFalse() {
        List<Schedule> scheduleList = new ArrayList<>();

        scheduleList.add(new Schedule());

        CustomScheduleManager customScheduleManager1 = new CustomScheduleManager(scheduleList);

        assertFalse(customScheduleManager1.viewSchedule(0));
    }

    @Test
    public void nonEmptyScheduleListDisplayRankingsShouldReturnTrue() {
        assertTrue(customScheduleManager.displayRankings());
    }

    @Test
    public void emptyScheduleListDisplayRankingsShouldReturnFalse() {
        CustomScheduleManager customScheduleManager1 = new CustomScheduleManager();

        assertFalse(customScheduleManager1.displayRankings());
    }

    @Test
    public void changeRankingsShouldSetCorrectRank() {
        Schedule scheduleMock = mock(Schedule.class);

        List<Schedule> scheduleList = new ArrayList<>();

        scheduleList.add(scheduleMock);

        CustomScheduleManager customScheduleManager1 = new CustomScheduleManager(scheduleList);

        PowerMockito.mockStatic(ScannerUtils.class);

        PowerMockito.when(ScannerUtils.getInt()).thenReturn(1);

        customScheduleManager1.changeRankings(0);

        verify(scheduleMock).setRank(1);
    }

    @Test
    public void checkScheduleExistsShouldReturnTrue() throws Exception {
        assertTrue((boolean) Whitebox.invokeMethod(customScheduleManager, "checkScheduleExists", "Schedule 1"));
    }

    @Test
    public void checkScheduleExistsShouldReturnFalse() throws Exception {
        assertFalse((boolean) Whitebox.invokeMethod(customScheduleManager, "checkScheduleExists", "Schedule 99"));
    }
}
