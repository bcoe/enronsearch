// Hit's Java API and performs searches 
// on Enron dataset.
function EnronSearch(opts) {
  _.extend(this, {
    searchInput: null, // The search input field.
    searchResults: null, // The search results div.
    highlights: null, // The suggestion area.
    terms: null, // The terms currently being search for.
    clear: null, // the clear search button.
    searchTerms: [], // search terms.
    nonce: 0, // used to prevent multiple searches.
  }, opts);

  this.typeAheadSearch();
  this.clearSearch();
}

// Clear current search.
EnronSearch.prototype.clearSearch = function() {
  var _this = this;

  this.clear.click(function() {
    _this.searchTerms = [];
    _this.highlights.html('');
    _this.terms.text('');
    _this.search();
    return false;
  });
}

// Wire up the type-ahead search.
EnronSearch.prototype.typeAheadSearch = function() {
  var _this = this,
    data = null;

  this.searchInput.keydown(function(e) {
    if (e.keyCode === 13 && _this.searchInput.val().length) {
      // AND together search terms as enter is pressed.

      if (_this.highlights.text().length) {
        _this.searchTerms.push(_this.highlights.text());
      } else {
        _this.searchTerms.push(_this.searchInput.val());
      }

      _this.searchInput.val(''); // reset search field.
      _this.terms.text( _this.searchTerms.join(', ') );
    }

    _this.search();
  });
};

// Hit our Java Controller for Search results,
// throttled to once every 250ms.
EnronSearch.prototype.search = function() {
  this.safeSearch( ( this.nonce = (this.nonce++) ) );
};

EnronSearch.prototype.safeSearch = function(nonce) {
  var _this = this;

  // only search once every 250ms.
  setTimeout(function() {

    if (nonce !== _this.nonce) return;

    // Build query from previously entered searches
    // and from current value of search field.
    var terms = [].concat(_this.searchTerms);
    if (_this.searchInput.val()) terms.push( _this.searchInput.val() + '*');
    var query = terms.join(' AND ');

    // Clear search results if query is blank.
    if (!query) {
      _this.displaySearchResults( {hits: {hits: [], total: 0}} );
      _this.highlights.html('');
      return;
    }

    data = {q: query};

    $.ajax({
      method: 'get',
      url: '/search',
      data: data,
      success: function(results) {
        if (nonce == _this.nonce) {
          _this.displaySearchResults(results);
          _this.displayHighlighted(results);
        }
      }
    });

  }, 250);
};

// Display search results in UI.
EnronSearch.prototype.displaySearchResults = function(results) {
  var _this = this;

  this.searchResults.html('<span class="count">' + results.hits.total + ' search results</span>');

  results.hits.hits.forEach(function(hit) {
    var message = hit._source,
      element = $('<div class="search-results">\
        <b class="subject"></b><br />\
        <b>to: </b><i class="to"></i>\
        <b>from: </b><i class="from"></i>\
        <br />\
        <p class="body"></p>\
        <hr />\
      </div>');

    element.find('.subject').text(message.subject);
    element.find('.to').text(message.to);
    element.find('.from').text(message.from);
    element.find('.body').text(message.body.replace(/[\r\n]/, ' ').substring(0, 4096) + '…');

    _this.searchResults.append(element);
  });
};

// Suggest emails that we might filter by, based on partial
// matches in to and from field.
EnronSearch.prototype.displayHighlighted = function(results) {

  var _this = this;

  this.highlights.html('');

  if (this.searchInput.val().length && results.hits.hits.length && results.hits.hits[0].highlight) {

    var toHighlight = results.hits.hits[0].highlight.to,
      highlight = null,
      from = results.hits.hits[0]._source.from;

    if (toHighlight) {

      // use jQuery to tokenize terms.
      var candidates = $( '<div>' + toHighlight.shift() + '</div>').find('em');

      _(candidates).each(function(candidate) {
        var candidate = $(candidate).text();

        if (_this.searchTerms.indexOf(candidate) === -1) {
          highlight = candidate;
          return;
        }
      });
    } else if ( from.indexOf(this.searchInput.val()) > -1 ) {
      highlight = from;
    }

    // Don't highlight an email we already scope search
    // results to.
    if (highlight) this.highlights.text(highlight);
  }
};