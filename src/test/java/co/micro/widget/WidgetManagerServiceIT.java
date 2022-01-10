package co.micro.widget;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;
import co.micro.widget.helpers.WidgetHelper;
import co.micro.widget.service.WidgetManagerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static co.micro.widget.helpers.WidgetHelper.checkWidget;
import static co.micro.widget.helpers.WidgetHelper.getCreateRequest;
import static co.micro.widget.helpers.WidgetHelper.getUpdateRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WidgetManagerServiceIT {

    private static final String URL = "/api/widgets";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createWidget() throws Exception {
        CreateWidget request = getCreateRequest("Widget_1", 9, 8, 1, 10, 20);

        createWidget(request);
        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 1);
        checkWidget(actualWidgets.get(0), WidgetHelper.getWidget("Widget_1", 9, 8, 1, 10, 20));
    }

    @Test
    public void updateWidget() throws Exception {
        createWidget(getCreateRequest("Widget_1", 9, 8, 1, 10, 20));
        UUID widgetId = getWidgets().get(0).getWidgetId();

        updateWidget(getUpdateRequest("Widget_2", 10, 4, 2, 20, 30, widgetId));
        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 1);
        checkWidget(actualWidgets.get(0), WidgetHelper.getWidget("Widget_2", 10, 4, 2, 20, 30));
    }

    @Test
    public void deleteWidget() throws Exception {
        createWidget(getCreateRequest("Widget_1", 9, 8, 1, 10, 20));
        UUID widgetId = this.getWidgets().get(0).getWidgetId();

        deleteWidget(widgetId);
        List<Widget> actualWidgets = getWidgets();

        assertTrue(actualWidgets.isEmpty());
    }

    @Test
    public void getWidget() throws Exception {
        createWidget(getCreateRequest("Widget_1", 9, 8, 1, 10, 20));
        UUID widgetId = getWidgets().get(0).getWidgetId();

        String json = getWidget(widgetId).getResponse().getContentAsString();
        Widget actualWidget = objectMapper.readValue(json, Widget.class);

        checkWidget(actualWidget, WidgetHelper.getWidget("Widget_1", 9, 8, 1, 10, 20));
    }

    @Test
    public void getWidgetsWithRowLimit() throws Exception {
        createWidget(getCreateRequest("Widget_1", 50, 50, 1, 100, 100, UUID.randomUUID()));
        createWidget(getCreateRequest("Widget_2", 50, 150, 2, 100, 100, UUID.randomUUID()));
        createWidget(getCreateRequest("Widget_3", 100, 100, 3, 100, 100, UUID.randomUUID()));
        createWidget(getCreateRequest("Widget_4", 50, 100, 4, 100, 100, UUID.randomUUID()));
        createWidget(getCreateRequest("Widget_5", 100, 150, 5, 100, 100, UUID.randomUUID()));

        String json = getWidgets(2).getResponse().getContentAsString();
        List<Widget> actualWidgets = Arrays.asList(objectMapper.readValue(json, Widget[].class));

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 2);
        checkWidget(
            actualWidgets.get(0),
            WidgetHelper.getWidget("Widget_1", 50, 50, 1, 100, 100)
        );
        checkWidget(
            actualWidgets.get(1),
            WidgetHelper.getWidget("Widget_2", 50, 150, 2, 100, 100)
        );
    }

    private MvcResult createWidget(CreateWidget request) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(URL)
                .contentType(APPLICATION_JSON_UTF8)
                .content(objToJsonString(request)))
            .andExpect(status().isCreated())
            .andReturn();
    }

    private MvcResult updateWidget(UpdateWidget request) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .patch(getUrlWithId(request.getWidgetId()))
                .contentType(APPLICATION_JSON_UTF8)
                .content(objToJsonString(request)))
            .andExpect(status().isOk())
            .andReturn();
    }

    private MvcResult deleteWidget(UUID widgetId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .delete(getUrlWithId(widgetId)))
            .andExpect(status().isOk())
            .andReturn();
    }

    private MvcResult getWidget(UUID widgetId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get(getUrlWithId(widgetId)))
            .andExpect(status().isOk())
            .andReturn();
    }

    private List<Widget> getWidgets() throws Exception {
        String json = getWidgets(WidgetManagerService.ROW_LIMIT_DEFAULT).getResponse().getContentAsString();
        return Arrays.asList(objectMapper.readValue(json, Widget[].class));
    }

    private MvcResult getWidgets(int limit) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get(URL)
                .param("limit", String.valueOf(limit)))
            .andExpect(status().isOk())
            .andReturn();
    }

    private String objToJsonString(Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    private static String getUrlWithId(UUID widgetId) {
        return String.format("%s/%s", URL, widgetId);
    }
}
