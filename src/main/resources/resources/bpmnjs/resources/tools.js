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

  /**
   * 上传bpmn
   * @param {object} bpmnModeler bpmn对象
   * @param {object} container 容器对象
   */
  upload(bpmnModeler, container) {
    var FileUpload = document.myForm.uploadFile.files[0];
    var fm = new FormData();
    fm.append('processFile', FileUpload);
    $.ajax({
      url: publicurl+'processDefinition/upload',
      type: 'POST',
      data: fm,
      async: false,
      contentType: false, //禁止设置请求类型
      processData: false, //禁止jquery对DAta数据的处理,默认会处理
      success: function (result) {
        var url = publicurl+'bpmn/' + result.obj
        tools.openFromUrl(bpmnModeler, container, url)
      },
      error: function (err) {
        console.log(err)
      }
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
}
export default tools