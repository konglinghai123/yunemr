package jhmk.clinic.cms.controller.ruleService;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import jhmk.clinic.core.config.CdssConstans;
import jhmk.clinic.entity.bean.Menzhenzhenduan;
import jhmk.clinic.entity.bean.Misdiagnosis;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.*;

import static jhmk.clinic.core.util.MongoUtils.getCollection;

/**
 * @author ziyu.zhou
 * @date 2018/8/7 13:42
 * 门诊诊断
 */

@Service
public class MzzdService {
    //入院记录
    static MongoCollection<Document> menzhenzhenduan = getCollection(CdssConstans.DATASOURCE, CdssConstans.MENZHENZHENDUAN);
    /**
     * 根据id获取既往史
     *
     * @return
     */
    public List<Menzhenzhenduan> getMenzhenzhenduanById(String id) {
        List<Menzhenzhenduan> list = new ArrayList<>();

        List<Document> countPatientId = Arrays.asList(
                new Document("$unwind", "$menzhenzhenduan"),
                new Document("$match", new Document("_id", id)),
                new Document("$project", new Document("_id", 1).append("patient_id", 1).append("visit_id", 1).append("menzhenzhenduan", 1))
        );
        AggregateIterable<Document> binli = menzhenzhenduan.aggregate(countPatientId);
        for (Document document : binli) {
            Menzhenzhenduan bean = new Menzhenzhenduan();
            Document binglizhenduan = (Document) document.get("menzhenzhenduan");
            String diagnosis_name = binglizhenduan.getString("diagnosis_name");
            String diagnosis_desc = binglizhenduan.getString("diagnosis_desc");
            if (diagnosis_name != null) {
                bean.setDiagnosis_name(diagnosis_name);
            } else {
                bean.setDiagnosis_name(diagnosis_desc);
            }
            bean.setDiagnosis_time(binglizhenduan.getString("diagnosis_time"));
            bean.setDiagnosis_num(binglizhenduan.getString("diagnosis_num"));
            bean.setDiagnosis_type_name(binglizhenduan.getString("diagnosis_type_name"));
            list.add(bean);
        }
        return list;
    }


    /**
     * 根据id获取门诊数据
     *
     * @param id
     * @return
     */
    public Misdiagnosis getMenzhenzhenduan(String id) {
        Misdiagnosis jiwangshi = new Misdiagnosis();
        List<String> jwDiseases = new LinkedList<>();
        List<Document> countPatientId2 = Arrays.asList(
                //过滤数据
                new Document("$match", new Document("_id", id)),
                new Document("$unwind", "$menzhenzhenduan")
        );
        AggregateIterable<Document> output = menzhenzhenduan.aggregate(countPatientId2);

        for (Document document : output) {

            Document menzhenzhenduanDoc = (Document) document.get("menzhenzhenduan");
            if (menzhenzhenduanDoc != null) {
                String diagnosis_desc = menzhenzhenduanDoc.getString("diagnosis_desc");
                String diagnosis_name = menzhenzhenduanDoc.getString("diagnosis_name");

            }
        }

        return jiwangshi;
    }

    private final String all = "既有诊断名称又有诊断描述的数量";
    private final String name = "只有诊断名称的数量";
    private final String desc = "只有诊断描述的数量";
    private final String allnot = "两者都没有的数量";
    private final String error = "门诊诊断是空的";

    public Map<String, Integer> getMenzhenzhenduanByIdList(List<String> idList) {
        Map<String, Integer> map = new HashMap<>();
        map.put(all, 0);
        map.put(name, 0);
        map.put(desc, 0);
        map.put(allnot, 0);
        map.put(error, 0);
        Misdiagnosis jiwangshi = new Misdiagnosis();
        List<String> jwDiseases = new LinkedList<>();
        for (String id : idList) {

            List<Document> countPatientId2 = Arrays.asList(
                    //过滤数据
                    new Document("$match", new Document("_id", id)),
                    new Document("$unwind", "$menzhenzhenduan")
            );
            AggregateIterable<Document> output = menzhenzhenduan.aggregate(countPatientId2);

            for (Document document : output) {

                Document menzhenzhenduanDoc = (Document) document.get("menzhenzhenduan");
                if (menzhenzhenduanDoc != null) {
                    String diagnosis_desc = menzhenzhenduanDoc.getString("diagnosis_desc");
                    String diagnosis_name = menzhenzhenduanDoc.getString("diagnosis_name");
                    String diagnosis_num = menzhenzhenduanDoc.getString("diagnosis_num");
                    if ("1".equals(diagnosis_num)) {

                        //既有诊断名称又有诊断描述的数量
                        if (StringUtils.isNotBlank(diagnosis_name) && StringUtils.isNotBlank(diagnosis_desc)) {
                            map.put(all, map.get(all) + 1);

                            //只有诊断名称的数量
                        } else if (StringUtils.isNotBlank(diagnosis_name) && StringUtils.isBlank(diagnosis_desc)) {
                            map.put(name, map.get(name) + 1);

                            //只有诊断描述的数量
                        } else if (StringUtils.isBlank(diagnosis_name) && StringUtils.isNotBlank(diagnosis_desc)) {
                            map.put(desc, map.get(desc) + 1);

                            //两者都没有的数量
                        } else if (StringUtils.isBlank(diagnosis_name) && StringUtils.isBlank(diagnosis_desc)) {
                            map.put(allnot, map.get(allnot) + 1);
                        } else {
                            map.put(error, map.get(error) + 1);

                        }
                    }

                }
            }
        }


        return map;
    }


    public Map<String, Integer> getDiagnosisCountByIdList(List<String> idList) {
        Map<String, Integer> map = new HashMap<>();
        for (String id : idList) {
            List<Document> countPatientId2 = Arrays.asList(
                    //过滤数据
                    new Document("$match", new Document("_id", id)),
                    new Document("$unwind", "$menzhenzhenduan")
            );
            AggregateIterable<Document> output = menzhenzhenduan.aggregate(countPatientId2);
            for (Document document : output) {
                Document menzhenzhenduanDoc = (Document) document.get("menzhenzhenduan");
                if (menzhenzhenduanDoc != null) {
                    String diagnosis_desc = menzhenzhenduanDoc.getString("diagnosis_desc");
                    String diagnosis_name = menzhenzhenduanDoc.getString("diagnosis_name");
                    String diagnosis_num = menzhenzhenduanDoc.getString("diagnosis_num");
                    if ("1".equals(diagnosis_num)) {
                        if (StringUtils.isNotBlank(diagnosis_name)) {
                            if (map.containsKey(diagnosis_name)) {
                                map.put(diagnosis_name, map.get(diagnosis_name) + 1);
                            } else {
                                map.put(diagnosis_name, 1);
                            }
                        } else {
                            if (StringUtils.isNotBlank(diagnosis_desc)) {
                                if (map.containsKey(diagnosis_desc)) {
                                    map.put(diagnosis_desc, map.get(diagnosis_desc) + 1);
                                } else {
                                    map.put(diagnosis_desc, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

}
