import $ from 'jquery';

const proHost = window.location.protocol + "//" + window.location.host;
const href = window.location.href.split("bpmnjs")[0];
const key = href.split(window.location.host)[1];
const publicurl = proHost + key;
const tools = {

  /**
   * 下载bpmn
   * @param {object} bpmnModeler bpmn对象
   */
  download(bpmnModeler) {
    const downloadLink = $("#downloadBPMN");
    bpmnModeler.saveXML({format: true}, function (err, xml) {
      if (err) {
        return console.error('could not save BPMN 2.0 diagram', err);
      }
      tools.setEncoded(downloadLink, 'diagram.bpmn', err ? null : xml);
    });
    // alert("2222")
  },

  setEncoded(link, name, data) {
    const encodedData = encodeURIComponent(data);

    if (data) {
      link.addClass('active').attr({
        'href': 'data:application/bpmn20-xml;charset=UTF-8,' + encodedData,
        'download': name
      });
    } else {
      link.removeClass('active');
    }
  }

}
export default tools