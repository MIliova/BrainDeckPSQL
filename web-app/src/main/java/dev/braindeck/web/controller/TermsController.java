package dev.braindeck.web.controller;

import dev.braindeck.web.client.BadRequestException;
import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.controller.payload.ImportTermPayload;
import dev.braindeck.web.entity.ImportTermDto;
import dev.braindeck.web.utills.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TermsController {

    private final SetsRestClient setsRestClient;

//    @PostMapping(value = "/terms/prepare-import")
//    public List<ImportTermDto> prepareImport (ImportTermPayload payload ) {
//        return this.setsRestClient.prepareImport(
//                payload.getText(), payload.getColSeparator(), payload.getRowSeparator(), payload.getColCustom(), payload.getRowCustom());
//    }

//    @GetMapping(value = "/terms/prepare-import")
//    public String testImport (ImportTermPayload payload, Model model ) {
//        try {
//            List<ImportTermDto> importTermDto =  this.setsRestClient.prepareImport(
//                "das Unternehmen\tпредприятие;die Reisegewohnheit, -en\tпривычки путешествий;die hochwassergefährdeten Gebiete\tрайоны, подверженные наводнениям;ausbauen\tрасширять, развивать;die Wohnungsknappheit, -en\tнехватка жилья;zurückgedrängt sein\tбыть отброшенным назад;großartig\tвеликолепный;hervorragend\tвыдающийся;bedenklich\tсомнительный, рискованный;halten für\tсчитаться чем-либо, принимать за\n" +
//                        "\n" +
//                        "ich halte es für bedenklich, dass;die Veränderung -en\tизменение;wagen + Akk\tотваживаться на ч-л, дерзать\n" +
//                        "\n" +
//                        "eine Veräderung wagen;der Weg\tдорога, путь\n" +
//                        "\n" +
//                        "einen neuen Weg gehen;gestalten\tоформлять, придавать вид\n" +
//                        "\n" +
//                        "das Leben neu gestalten;schwerfallen, \n" +
//                        "\n" +
//                        "fällt, fiel, ist schwergefallen\tдаваться с трудом\n" +
//                        "\n" +
//                        "Es fällt mir schwer, neue Weg zu gehen;vertraut\tблизкий, хорошо знакомый\n" +
//                        "\n" +
//                        "vertraute Wege;einschlagen, \n" +
//                        "\n" +
//                        "schlägt, schlug, hat eingeschlagen\tвбивать, разбивать, ударять\n" +
//                        "\n" +
//                        "eine neue Richtung einschlagen;der Grund -ë für +Akk\tпричина;vielfältig\tразнообразный\n" +
//                        "\n" +
//                        "Die Gründe sind vielfältig;die Angst, -ë vor + Dativ\tстрах;der Misserfolg, -e\tнеудача;hinterfragen\tставить под вопрос\n" +
//                        "\n" +
//                        "Das Leben hinterfragen;der Pädagoge, -n\n" +
//                        "die Pädagogin, -nen\tпедагог;die Großstadt, -ë\tбольшой город;sich sehnen nach + Dat\tтосковать по;verwirklichen\tосуществлять, претворять в жизнь\n" +
//                        "\n" +
//                        "einen Traum verwirklichen;die Begeisterung für + Akk\tвоодушевление;die Anregung, -en\tпобуждение, толчок, импульс;verbunden (mit)+ Dat\tсвязанный\n" +
//                        "\n" +
//                        "Ich fühle mich mit der Natur verbunden.;die Welle, -n\tволна;die Ausbildung, -en zu + Dat\tобразование\n" +
//                        "\n" +
//                        "Sie macht eine Ausbildung zur Bürokauffrau.;eingesperrt\tвзаперти\n" +
//                        "\n" +
//                        "sich eingesperrt fühlen;der Mut\tмужество, смелость;zusammennehmen, \n" +
//                        "\n" +
//                        "nimmt, nahm, hat zusammengenommen\tсобраться\n" +
//                        "\n" +
//                        "all seinen Mut zusammennehmen;der Lebensunterhalt\tсредства к существованию\n" +
//                        "\n" +
//                        "sich seinen Lebensunterhalt;die Nähe + Dat\tблизость\n" +
//                        "\n" +
//                        "die Nähe zur Natur;der Unternehmensberater, -\tбизнес-консультант;die Führungsposition, -en\tруководящая должность;der Druck\tдавление, нажим\n" +
//                        "\n" +
//                        "unter Druck stehen;überleben\tпережить;der Schock\tшок, потрясение;entscheidend\tрешающий;der Wendepunkt, -e\tпереломный момент\n" +
//                        "\n" +
//                        "der entscheidende Wendepunkt in meinem Leben;meditieren\tмедитировать;die Einstellung, -en\tточка зрения\n" +
//                        "\n" +
//                        "seine Einstellung zu etw. ändern;bewusst\tсознательный;umstellen\tпереставлять, реорганизовывать\n" +
//                        "\n" +
//                        "seine Ernährung umstellen;ankommen \n" +
//                        "\n" +
//                        "(kommt an, kam an, ist angekommen)\tвоспринимать\n" +
//                        "\n" +
//                        "Nein zu sagen, kommt nicht immer gut an.;auswandern\tэмигрировать;sich anlügen, \n" +
//                        "\n" +
//                        "lügt, log, hat angelogen\tлгать самому себе\n" +
//                        "\n" +
//                        "Manche leute lügen sich selbst an.;etw zugeben, \n" +
//                        "\n" +
//                        "gibt, gab, hat zugegeben\tпризнавать;zusammenhängen mit + Dat, \n" +
//                        "\n" +
//                        "hängt, hing, hat zusammengehangen\tбыть связанным c;",
//                "tab", ";", "-", "\n\n");
//        } catch(BadRequestException e) {
//
//            List<FieldErrorDto> errorDtos = Util.problemDetailErrorToDtoList(e.getJsonObject());
//            model.addAttribute("errors", errorDtos);
//        }
//        return "";
//    }
}
