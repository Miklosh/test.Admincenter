package com.engagepoint.admincenter.web.uib;

import com.engagepoint.component.menu.model.BootstrapDefaultMenuModel;
import org.primefaces.component.menuitem.UIMenuItem;
import org.primefaces.model.menu.MenuModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

@ManagedBean(name = "topMenu")
@RequestScoped
public class TopMenuBean {

    private static final String ACTIVE = "active";
    public static final String EDITOR_URL = "/pages/preferencesEditor.xhtml";
    public static final String SETTINGS_URL = "/pages/settings.xhtml";
    private MenuModel model;

    @PostConstruct
    public void initModel() {
        model = new BootstrapDefaultMenuModel();
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        String viewId = viewRoot.getViewId();
        model.addElement(getEditorItem(viewId, null));
        model.addElement(getSettingsItem(viewId, null));
    }

    public static UIMenuItem getEditorItem(String viewId, String itemId) {
        UIMenuItem item = getMenuItem(viewId, itemId, "Preferences Editor", EDITOR_URL);
        if (EDITOR_URL.equals(viewId)) {
            item.setStyleClass(ACTIVE);
        }
        return item;
    }

    public static UIMenuItem getSettingsItem(String viewId, String itemId) {
        UIMenuItem item = getMenuItem(viewId, itemId, "Settings", SETTINGS_URL);
        if (SETTINGS_URL.equals(viewId)) {
            item.setStyleClass(ACTIVE);
        }
        return item;
    }

    public static UIMenuItem getMenuItem(String viewId, String itemId, String itemName, String url) {
        UIMenuItem item = new UIMenuItem();
        item.setValue(itemName);
        item.setUrl(url);
        if (itemId != null) {
            item.setId(itemId);
        }
        item.setIncludeViewParams(true);
        return item;
    }

    public MenuModel getModel() {
        return model;
    }
}
