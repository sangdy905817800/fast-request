package io.github.kings1990.plugin.fastrequest.configurable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.intellij.util.ui.ListTableModel;
import io.github.kings1990.plugin.fastrequest.config.FastRequestComponent;
import io.github.kings1990.plugin.fastrequest.model.DataMapping;
import io.github.kings1990.plugin.fastrequest.model.FastRequestConfiguration;
import io.github.kings1990.plugin.fastrequest.view.AbstractConfigurableView;
import io.github.kings1990.plugin.fastrequest.view.sub.DataMappingConfigView;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class DataMappingConfigurable extends AbstractConfigConfigurable {
    protected FastRequestConfiguration config = FastRequestComponent.getInstance().getState();
    private DataMappingConfigView view = new DataMappingConfigView(config);

    @Override
    public AbstractConfigurableView getView() {
        return view;
    }

    @Override
    public @Nullable
    JComponent createComponent() {
        return view.getComponent();
    }

    @Override
    public String getDisplayName() {
        return "Date Type Config";
    }

    @Override
    public boolean isModified() {
        List<DataMapping> currentCustomDataMappingList = view.getViewCustomDataMappingList();
        List<DataMapping> customDataMappingList = config.getCustomDataMappingList();
        Integer randomStringLength = config.getRandomStringLength();
        Integer viewRandomStringLength = Integer.parseInt(view.getRandomStringTextField().getText());
        return !randomStringLength.equals(viewRandomStringLength) || !judgeEqual(currentCustomDataMappingList, customDataMappingList);
    }

    @Override
    public void apply() {
        List<DataMapping> viewCustomDataMappingList = view.getViewCustomDataMappingList();
        List<DataMapping> changeCustomDataMappingList = JSONArray.parseArray(JSON.toJSONString(viewCustomDataMappingList), DataMapping.class);
        config.setCustomDataMappingList(changeCustomDataMappingList);
        int viewRandomStringLength = Integer.parseInt(view.getRandomStringTextField().getText());
        config.setRandomStringLength(viewRandomStringLength);
    }

    @Override
    public void reset() {
        super.reset();
        List<DataMapping> oldCustomDataMappingList = config.getCustomDataMappingList();
        List<DataMapping> oldCustomDataMappingListNew = JSONArray.parseArray(JSON.toJSONString(oldCustomDataMappingList), DataMapping.class);
        int randomStringLength = config.getRandomStringLength();
        view.setViewCustomDataMappingList(oldCustomDataMappingListNew);
        view.getCustomTable().setModel(new ListTableModel<>(view.getColumnInfo(), oldCustomDataMappingListNew));
        view.getRandomStringTextField().setText(randomStringLength + "");
    }

    public boolean judgeEqual(List<DataMapping> list1, List<DataMapping> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            DataMapping dataMapping1 = list1.get(i);
            DataMapping dataMapping2 = list2.get(i);
            if (!dataMapping1.getType().equals(dataMapping2.getType()) || !dataMapping1.getValue().equals(dataMapping2.getValue())) {
                return false;
            }
        }
        return true;
    }

}
