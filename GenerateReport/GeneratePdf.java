package br.com.sisprog.service;

import br.com.sisprog.entities.cfin.boletos.BoletoEntity;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author C�sar J�nior
 */
public class GeneratePdf {

    public static byte[] getPdf(List<?> data, Map<String, Object> params, URL addressReport) throws Exception {
        try {
            params.put("logo", GeneratePdf.class.getResource("/logos/" + params.get("sigla")) + "/logo.png");

            //Endereço da pasta aonde fica os header e foter padrões
            String enderecoTemplate = GeneratePdf.class.getResource("templates/footer.jasper").getFile();
            if (enderecoTemplate != null) {
                params.put("enderecoTemplate", enderecoTemplate.replace("footer.jasper", ""));
            }

            try {
                return JasperRunManager.runReportToPdf(addressReport.getFile(), params, new JRBeanCollectionDataSource(data));
            } catch (JRException e) {
                throw new Exception("Erro geração RELATORIO de " + params.get("nomeModulo") + "..." + e.getMessage());
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    public static byte[] getBoleto(BoletoEntity boletoEntity) {
        byte[] bytes = null;
        
        String addressPath = GeneratePdf.class.getResource("/boleto/boleto.jrxml").getFile();
        if (boletoEntity.getBanco().getCodigo().contains("001")) {
            boletoEntity.getBanco().setLogo(addressPath.replace("boleto.jrxml", "LogoBB.jpg"));
        } else {
            boletoEntity.getBanco().setLogo(addressPath.replace("boleto.jrxml", "logoCaixa.jpg"));
        }
        
        List<BoletoEntity> lst = new ArrayList<>();
        lst.add(boletoEntity);
                
        try {
            JasperReport report = JasperCompileManager.compileReport(addressPath);
            bytes = JasperRunManager.runReportToPdf(report, null, new JRBeanCollectionDataSource(lst));
        } catch (JRException ex) {
            Logger.getLogger(GeneratePdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return bytes;
    }

}
