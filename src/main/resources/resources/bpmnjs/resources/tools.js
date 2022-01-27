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

  /**
   * 保存bpmn对象
   * @param {object} bpmnModeler bpmn对象
   */
  saveBpmn(bpmnModeler) {
    bpmnModeler.saveXML({format: true}, function (err, xml) {
      if (err) {
        return console.error('could not save bpmn', err);
      }
      console.log(xml)
      var param = {
        "stringBPMN": xml
      }
      $.ajax({
        url: publicurl + 'processDefinition/addDeploymentByString',
        type: 'POST',
        dataType: "json",
        data: param,
        success: function (result) {
          if (result.status === '0') {
            alert('BPMN部署成功');
          } else {
            alert(result.msg);
          }
        },
        error: function (err) {
          alert(err);
        }
      });
    });
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
  },

  /**
   * 隐藏弹出框
   * @param id
   */
  syhide(id) {
    if (typeof id == "undefined") {
      var dom = $(".sy-alert")
    } else {
      var dom = $("#" + id)
    }
    var name = dom.attr("sy-leave");
    dom.addClass(name);
    $(".sy-mask").fadeOut(300);
    setTimeout(function () {
      dom.hide();
      dom.removeClass(name);
    }, 300)
  },
}
export default tools