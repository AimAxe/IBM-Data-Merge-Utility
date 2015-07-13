/**
 * @jsx React.DOM
 */
var App = React.createClass({
  
  getInitialState: function() {
    return {
      selectedCollection: "root",
      selectedRibbonItem: null,
      selectedRibbonIndex: 0,
      data: [],
      directives: [],
      template: {'directives':[]}
    };
  },
  initRouter: function () {
    var routes = {
    };
    window.router = Router(routes);
    window.router.configure({html5history: supports_history_api()});
    window.router.init();
  },
  componentDidMount: function () {
    if(!this.props.suppressCollection){
      this.initRouter();
      this.loadDirectivesFromServer();
      this.loadCollectionsFromServer();
    }
    this.loadTemplatesFromServer("root");
  },
  handleCollectionSelected: function(selectedCollection) {
    this.loadTemplatesFromServer(selectedCollection);
  },
  handleRibbonSelected: function(selectedRibbonIndex,selectedRibbonItem) {
    //this.setState({selectedRibbonItem: selectedRibbonItem,selectedRibbonIndex: selectedRibbonIndex});
    this.loadTemplateFromServer(this.state.selectedCollection,
                                selectedRibbonItem['name'],
                                selectedRibbonItem['columnValue']);
  },
  loadCollectionsFromServer: function() {
    var selectedCollection = this.state.selectedCollection;
    var params = {};
    $.ajax({
      url: '/idmu/templates',
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        var uniques={};
        var first = data[0];
        for(var idx=0; idx < data.length; idx++) {
          var key = data[idx].collection;
          var name = data[idx].name;
          var columnValue = data[idx].columnValue;
          uniques[key] = {collection: key};
        }
        var final_list = Object.keys(uniques).map(function(v){ return {collection: v}});
        this.setState({data: final_list,selectedCollection: selectedCollection});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  loadTemplatesFromServer: function(collection) {
    var params = {};
    $.ajax({
      url: '/idmu/templates/'+collection,
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        this.setState({templates: data,selectedCollection: collection}, function(){
          if(!this.props.suppressCollection){
            var sel = this.state.selectedRibbonItem || data[0];
            this.loadTemplateFromServer(sel.collection,sel.name,sel.columnValue);
          }
        }.bind(this));
        
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  loadDirectivesFromServer: function() {
    var params = {};
    $.ajax({
      url: '/idmu/directives',
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        this.setState({directives: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  buildBodyItems: function(content){
    var result=[];
    var reg=new RegExp(/<tkBookmark/g);
    var items=[];
    var sIdx = 0;
    var eIdx = 0;
    var found = false;
    while((result = reg.exec(content)) !== null) {
      eIdx = content.indexOf("/>",result.index);
      if(eIdx!=-1){
        var slice1 = content.slice(sIdx,result.index);
        var slice2 = content.slice(result.index,eIdx+2);
        sIdx = eIdx+2;
        items.push({type: 'text',slice: slice1});
        items.push({type: 'bookmark',slice: slice2});
      }
    }
    var slice = content.slice(sIdx);
    if(slice && slice.length > 0){
                                  items.push({type: 'text', slice: slice});
                                  }
    return items;
  },
  loadTemplateFromServer: function(collection,id,columnValue) {
    var params = {};
    if(columnValue){
      params['columnValue'] = columnValue;
    }
    $.ajax({
      url: '/idmu/templates/'+collection+'/'+id,
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
                                                               data.items = this.buildBodyItems(data.content);

                                                               var text = data.content.replace(/\<tkBookmark/g,"\<div class=\"tkbookmark\"  contenteditable=\"false\"");
            data.content = text;
            this.setState({template: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  moveItemWithinList: function(itemId, listId, newIndex){
    console.log('moving '+itemId+' to '+listId+':'+newIndex);
    //Probably want to fire an action creator here... but we'll just splice state manually
    var tpl = this.state.template;
    var newListInfo = $.extend(true, [], tpl['directives']);
    var oldItemArr = newListInfo;
    var item = oldItemArr.splice(itemId,1);
    oldItemArr.splice(newIndex,0,item[0]);
    tpl.directives = oldItemArr;
    this.setState({template: tpl});
  },
  moveItemBetweenList: function(itemId, oldListId, oldIndex, newListId, newIndex){
    console.log('moving '+itemId+' from '+oldListId+':'+oldIndex+' to '+newListId+':'+newIndex);
    //Probably want to fire an action creator here... but we'll just splice state manually
    var newListInfo = $.extend(true, [], this.state.directives);
    var oldItemArr = newListInfo;
    var item = oldItemArr.splice(oldIndex,1);
    var tpl = this.state.template;
    var newItemArr = tpl.directives;
    newItemArr.splice(newIndex,0,item[0]);
    tpl.directives = newItemArr;
    this.setState({template: tpl});
  },
  saveDirective: function(index,payload){
    console.debug("save directive at "+index);
    var tpl = this.state.template;
    var directives = tpl.directives;
    directives[index] = payload;
    this.setState({template: tpl});
  },
  saveTemplateToServer: function(opts,collection) {
    console.log("save template...");

    var params = {template:{}};
    params.template = $.extend({},this.state.template,opts);
    $.ajax({
      url: '/idmu/templates/'+collection,
      dataType: 'json',
      method: 'PUT',
      cache: false,
      data: params,
      success: function(data) {
        this.setState({template: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  handleSave: function(opts) {
    this.saveTemplateToServer(opts,this.state.selectedCollection);
    this.loadCollectionsFromServer();
    this.loadTemplatesFromServer(this.state.selectedCollection);
  },
  render: function() {
    return (
      <div className="app_view">
        <div id="template_collection" className="row template_collection">
          {this.header()}
        </div>
        <div id="template_ribbon" className="row template_ribbon">
          <div>
                      {this.ribbon()}
          </div>
        </div>
      </div>
    );
  },

  header: function(){
                     if(!this.props.suppressCollection){
                       return(<TemplateCollection suppress={this.props.suppressCollection} selectHandler={this.handleCollectionSelected} data={this.state}/>);
                     }else{return (<div/>);}
},
      ribbon: function(){
                         if(!this.props.suppressRibbon){
                           var mCB = this.moveItemBetweenList;
                           var aCB = this.moveItemWithinList;
                           var sCB = this.handleSave;
                           var dCB = this.saveDirective;
                           return(<TemplateRibbon  suppressNav={this.props.suppressNav} initHandler={this.handleCollectionSelected} selectHandler={this.handleRibbonSelected} data={this.state} mCB={mCB} aCB={aCB} sCB={sCB} dCB={dCB}/>);
                         }else{
                           return(<div/>);
                         }
                         }
});
