/**
 * @jsx React.DOM
 */
var TemplateCollection = React.createClass({
  handleChange: function(event) {
    this.props.selectHandler(event.target.value);
  },
  render: function() {
    var
        data = this.props.data,
        selectedValue = data['selectedCollection'],
        collection = data.data;
    
    var options=collection.map(function(k,i){
      return (<option key={i} value={k.collection} label={k.collection}>{k.collection}</option>);
    });
    
    var id = "template-collection-"+this.props.level+"-"+this.props.index;
    return(
      <div className="row no-margin">
        <form className="form-vertical">
          <div className="form-group  col-sm-6">
            <label for={id} className="col-sm-4 control-label control-label-big">Collections</label>
            <div className="col-sm-12">
              <select id={id} className="form-control input-lg" onChange={this.handleChange} value={selectedValue}>
                {options}
              </select>
            </div>
          </div>
        </form>
      </div>
    );
  }
});
