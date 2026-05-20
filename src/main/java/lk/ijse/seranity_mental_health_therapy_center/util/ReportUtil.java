package lk.ijse.seranity_mental_health_therapy_center.util;

import lk.ijse.seranity_mental_health_therapy_center.db.FactoryConfiguration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.hibernate.Session;

import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ReportUtil {

    private static final String REPORT_BASE_PATH =
            "/lk/ijse/seranity_mental_health_therapy_center/report/";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        System.setProperty("net.sf.jasperreports.compiler.xml.parser.validating", "false");


        System.setProperty(
                "javax.xml.parsers.SAXParserFactory",
                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
        );


        System.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");


    }

    public static void showPatientReport() {
        showReport("PatientReport.jrxml", "Patient Report");
    }

    public static void showPaymentReport() {
        showReport("PaymentReport.jrxml", "Payment Report");
    }

    public static void showTherapySessionReport() {
        showReport("TherapySessionReport.jrxml", "Therapy Session Report");
    }

    public static void showTherapistPerformanceReport() {
        showReport("TherapistPerformanceReport.jrxml", "Therapist Performance Report");
    }

    private static void showReport(String jrxmlFileName, String reportTitle) {
        Session hibernateSession = null;
        try {
            hibernateSession = FactoryConfiguration.getInstance().getSession();

            Connection connection = hibernateSession.doReturningWork(conn -> conn);

            InputStream resourceStream = ReportUtil.class.getResourceAsStream(
                    REPORT_BASE_PATH + jrxmlFileName);

            if (resourceStream == null) {
                throw new JRException("Report file not found: " + REPORT_BASE_PATH + jrxmlFileName);
            }


            JasperReport jasperReport = JasperCompileManager.compileReport(resourceStream);

            Map<String, Object> params = new HashMap<>();
            params.put("generatedDate", LocalDateTime.now().format(DATE_FMT));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);


            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle(reportTitle);
            viewer.setVisible(true);

        } catch (JRException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to generate report: " + jrxmlFileName, e);
        } finally {
            if (hibernateSession != null && hibernateSession.isOpen()) {
                hibernateSession.close();
            }
        }
    }
}